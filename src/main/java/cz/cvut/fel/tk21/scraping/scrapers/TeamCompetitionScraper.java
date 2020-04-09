package cz.cvut.fel.tk21.scraping.scrapers;

import cz.cvut.fel.tk21.exception.WebScrapingException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.teams.Match;
import cz.cvut.fel.tk21.model.teams.Region;
import cz.cvut.fel.tk21.model.teams.Team;
import cz.cvut.fel.tk21.model.teams.TeamCompetition;
import cz.cvut.fel.tk21.model.tournament.AgeCategory;
import cz.cvut.fel.tk21.scraping.service.TeamCompetitionScrapingService;
import cz.cvut.fel.tk21.service.*;
import cz.cvut.fel.tk21.util.DateUtils;
import cz.cvut.fel.tk21.util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Component
public class TeamCompetitionScraper {

    private static final Logger logger = LoggerFactory.getLogger(TeamCompetitionScraper.class);
    private static final String baseUrl = "http://cztenis.cz";
    private static final Map<AgeCategory, String> urls = new HashMap<>();
    private static final Map<Integer, Region> indexMapping = new HashMap<>();

    private final TeamCompetitionService teamCompetitionService;
    private final TeamService teamService;
    private final ClubService clubService;
    private final MatchService matchService;
    private final UserService userService;
    private final TeamCompetitionScrapingService teamCompetitionScrapingService;

    @Autowired
    public TeamCompetitionScraper(TeamCompetitionService teamCompetitionService, TeamService teamService, ClubService clubService, MatchService matchService, UserService userService, TeamCompetitionScrapingService teamCompetitionScrapingService) {
        this.teamCompetitionService = teamCompetitionService;
        this.teamService = teamService;
        this.clubService = clubService;
        this.matchService = matchService;
        this.userService = userService;
        this.teamCompetitionScrapingService = teamCompetitionScrapingService;
        createUrlMap();
        createIndexMapping();
    }

    public void updateAllCompetitions() throws IOException, WebScrapingException {
        logger.trace("Team Competition scraping started");

        for (Map.Entry<AgeCategory, String> entry : urls.entrySet()) {
            Document document = Jsoup.connect(entry.getValue()).get();

            Document doc = loadCorrectYearDocument(document);
            int year = extractYear(doc);
            teamCompetitionScrapingService.deleteCompetitionsInYear(year - 1, entry.getKey());
            List<TeamCompetition> toBeFound = teamCompetitionService.findAllCompetitionsInYearAndCategory(year, entry.getKey());

            logger.trace("Scraping competitions in " + "year " + year + " category " + entry.getKey().toString());

            findAllCompetitionsInDocument(doc, entry.getKey(), year, toBeFound);

            logger.trace("Scraping competitions in " + "year " + year + " category " + entry.getKey().toString() + " finished");
        }

        logger.trace("Team Competition scraping finished");
    }

    private void findAllCompetitionsInDocument(Document doc, AgeCategory ageCategory, int year, List<TeamCompetition> toBeFound) throws IOException, WebScrapingException {
        Element competitionTable = doc.select("table tbody").first();
        assertNonNullElement(competitionTable, "Competition Table");
        Elements rows = competitionTable.select("tr");

        for (Element row : rows) {
            Elements cells = row.select("td");
            for (int i = 0; i < cells.size(); i++) {
                Element cell = cells.get(i);
                if(cell.html().equals("&nbsp;")) continue;
                String name = cell.select("a").html();
                if(indexMapping.get(i) == Region.CR && ageCategory != AgeCategory.ADULTS) continue;
                if(indexMapping.get(i) == Region.CR && ageCategory == AgeCategory.ADULTS
                        && !name.contains("2.liga") && !name.contains("2. liga")) continue;

                String link = cell.select("a").attr("href");
                int webId = Integer.parseInt(link.split("/")[link.split("/").length - 1]);

                TeamCompetition competition = new TeamCompetition();
                competition.setName(name);
                competition.setYear(year);
                competition.setAgeCategory(ageCategory);
                competition.setLink(link.isBlank() ? null : baseUrl + link);
                competition.setWebId(webId);
                competition.setRegion(indexMapping.get(i));

                TeamCompetition stored = toBeFound.stream().filter(c -> c.getWebId() == webId).findFirst().orElse(null);
                competition = teamCompetitionScrapingService.handleUpdate(competition, stored);
                toBeFound.remove(competition);

                if(competition != null){
                    teamCompetitionScrapingService.deleteAllTeamsAndMatchesInCompetition(competition);
                    findAllTeamsInCompetition(competition, year);
                }
            }
        }

        teamCompetitionScrapingService.deleteCompetitions(toBeFound);

    }

    private void findAllTeamsInCompetition(TeamCompetition competition, int year) throws IOException, WebScrapingException {
        Document doc = Jsoup.connect(competition.getLink()).get();

        Element teamTable = doc.select("table tbody").first();
        assertNonNullElement(teamTable, "Team Table");
        Elements rows = teamTable.select("tr");

        List<Team> teams = new ArrayList<>();
        for (Element row : rows) {
            Elements cells = row.select("td");
            int ranking = Integer.parseInt(cells.get(0).select("div").first().html());
            String name = cells.get(1).select("a").first().html();
            String link = cells.get(1).select("a").first().attr("href");
            int wins = Integer.parseInt(cells.get(3).select("div").first().html());
            int losses = Integer.parseInt(cells.get(4).select("div").first().html());
            int points = Integer.parseInt(cells.get(8).select("strong").first().html());

            Team team = new Team();
            team.setName(name);
            team.setCompetition(competition);
            team.setLink(link.isBlank() ? null : baseUrl + link);
            team.setLosses(losses);
            team.setPoints(points);
            team.setWins(wins);
            team.setRanking(ranking);
            team.setClub(extractClubFromName(name));

            team = addUsersToTeam(team);
            teams.add(team);

            teamService.persist(team);
        }

        findAllMatchesInCompetition(doc, teams, year);
    }

    private void findAllMatchesInCompetition(Document doc, List<Team> teams, int year) throws WebScrapingException {
        Element teamTable = doc.select("table tbody").get(1);
        assertNonNullElement(teamTable, "Schedule Table");
        Elements rows = teamTable.select("tr");

        LocalDate date = null;
        int round = 0;
        for (Element row : rows){
            Elements cells = row.select("td");

            int startIndex = 0;
            if(cells.size() == 9){
                round = Integer.parseInt(cells.get(0).select("div").first().html());
                String dateString = cells.get(1).select("div").first().html();
                date = this.extractDateFromString(dateString, year);
                startIndex = 2;
            }

            String homeName = null;
            String awayName = null;
            if(containStrongElem(cells.get(startIndex + 1))){
                homeName = cells.get(startIndex + 1).select("strong").html();
            } else {
                homeName = cells.get(startIndex + 1).select("a").html();
            }
            if(containStrongElem(cells.get(startIndex + 2))){
                awayName = cells.get(startIndex + 2).select("strong").html();
            } else {
                awayName = cells.get(startIndex + 2).select("a").html();
            }
            int homePoints = Integer.parseInt(cells.get(startIndex + 3).select("strong").html().split(":")[0]);
            int awayPoints = Integer.parseInt(cells.get(startIndex + 3).select("strong").html().split(":")[1]);
            String link = cells.get(startIndex + 6).select("a").attr("href");

            Match match = new Match();
            match.setHomeTeam(findTeamByName(teams, homeName));
            match.setAwayTeam(findTeamByName(teams, awayName));
            match.setHomePoints(homePoints);
            match.setAwayPoints(awayPoints);
            match.setRound(round);
            match.setDate(date);
            match.setDetailLink(link.isBlank() ? null : baseUrl + link);

            matchService.persist(match);
        }
    }

    private Team addUsersToTeam(Team team) throws IOException, WebScrapingException {
        Document doc = Jsoup.connect(team.getLink()).get();

        Element teamTable = doc.select("table tbody").get(2);
        assertNonNullElement(teamTable, "Team Members Table");
        Elements rows = teamTable.select("tr");

        for (Element row : rows) {
            Elements cells = row.select("td");
            if(cells.size() == 1) return team;

            String link = cells.get(1).select("a").first().attr("href");
            long webId = Long.parseLong(link.split("/")[link.split("/").length - 1]);

            Optional<User> user = userService.findUserByWebId(webId);
            user.ifPresent(team::addUser);
        }

        return team;
    }

    private LocalDate extractDateFromString(String date, int year){
        String dateOnly = date.split(" ")[0];
        if(dateOnly.split("\\.").length == 2){
            if(dateOnly.charAt(dateOnly.length()-1) == '.'){
                dateOnly += year;
            } else {
                dateOnly += "." + year;
            }
        }
        while(!Character.isDigit(dateOnly.charAt(dateOnly.length()-1))){
            dateOnly = dateOnly.substring(0, dateOnly.length() - 1);
        }
        LocalDate myDate = null;
        try{
            myDate = LocalDate.parse(dateOnly, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (DateTimeParseException e){
            try{
                myDate = LocalDate.parse(dateOnly, DateTimeFormatter.ofPattern("d.MM.yyyy"));
            } catch (DateTimeParseException ex) {
                try{
                    myDate = LocalDate.parse(dateOnly, DateTimeFormatter.ofPattern("dd.M.yyyy"));
                } catch (DateTimeParseException exe) {
                    myDate = LocalDate.parse(dateOnly, DateTimeFormatter.ofPattern("d.M.yyyy"));
                }
            }
        }
        return myDate;
    }

    private Team findTeamByName(List<Team> teams, String name){
        Optional<Team> team = teams.stream().filter(t -> t.getName().equals(name)).findFirst();
        if(team.isEmpty()) return null;
        return team.get();
    }

    private boolean containStrongElem(Element elem){
        return elem.select("strong").size() != 0;
    }

    private Club extractClubFromName(String name){
        String[] splitted = name.split(" ");
        String last = splitted[splitted.length-1];
        String clubName = "";
        if(StringUtils.isTeamLetter(last)){
            splitted[splitted.length-1] = "";
            clubName = String.join(" ", splitted);
            clubName = clubName.trim();
        } else {
            clubName = name;
        }

        return findClubByVariousPossibleNames(clubName);
    }

    private Club findClubByVariousPossibleNames(String clubName){
        if(clubName.equals("TC Brno")) clubName += ".";
        Optional<Club> club = clubService.findClubByName(clubName);
        if(club.isPresent()) return club.get();

        club = clubService.findClubByName(clubName + " z.s.");
        if(club.isPresent()) return club.get();

        club = clubService.findClubByName(clubName + ", z.s.");
        if(club.isPresent()) return club.get();

        club = clubService.findClubByName(clubName + ",z.s.");
        if(club.isPresent()) return club.get();

        club = clubService.findClubByName(clubName + " o.s.");
        if(club.isPresent()) return club.get();

        return null;
    }

    private Document loadCorrectYearDocument(Document doc) throws IOException, WebScrapingException {
        Element potentialForm = doc.select("form.well").first();
        assertNonNullElement(potentialForm, "Year selector");
        FormElement yearForm = (FormElement) potentialForm;

        //TODO change this
        int currentYear = 2019;
        //int currentYear = DateUtils.getCurrentYear();
        Element selectYear = yearForm.select("select").first();
        assertNonNullElement(selectYear, "Form select");
        Elements options = selectYear.select("option");
        Element correctYear = selectYear.select("option[label=" + currentYear +"]").first();

        if(correctYear == null) return doc;
        options.forEach(o -> o.attr("selected", false));
        correctYear.attr("selected", true);

        return yearForm.submit().post();
    }

    private int extractYear(Document doc) throws WebScrapingException {
        Element potentialForm = doc.select("form.well").first();
        assertNonNullElement(potentialForm, "Year selector");
        FormElement yearForm = (FormElement) potentialForm;

        Element selectYear = yearForm.select("select").first();
        assertNonNullElement(selectYear, "Form select");
        Element selected = selectYear.select("option[selected]").first();

        return Integer.parseInt(selected.attr("label"));
    }

    private void createUrlMap(){
        urls.put(AgeCategory.YOUNGER, "http://cztenis.cz/mladsi-zactvo/druzstva");
        urls.put(AgeCategory.OLDER, "http://cztenis.cz/starsi-zactvo/druzstva");
        urls.put(AgeCategory.JUNIOR, "http://cztenis.cz/dorost/druzstva");
        urls.put(AgeCategory.ADULTS, "http://cztenis.cz/dospeli/druzstva");
    }

    private void createIndexMapping(){
        indexMapping.put(0, Region.CR);
        indexMapping.put(1, Region.PRAHA);
        indexMapping.put(2, Region.STREDOCESKY);
        indexMapping.put(3, Region.JIHOCESKY);
        indexMapping.put(4, Region.ZAPADOCESKY);
        indexMapping.put(5, Region.SEVEROCESKY);
        indexMapping.put(6, Region.VYCHODOCESKY);
        indexMapping.put(7, Region.JIHOMORAVSKY);
        indexMapping.put(8, Region.SEVEROMORAVSKY);
    }

    private void assertNonNullElement(Element element, String name) throws WebScrapingException {
        if(element == null){
            throw new WebScrapingException("Unable to find element " + name);
        }
    }

}
