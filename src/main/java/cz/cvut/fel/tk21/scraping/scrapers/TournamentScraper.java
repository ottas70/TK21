package cz.cvut.fel.tk21.scraping.scrapers;

import cz.cvut.fel.tk21.exception.WebScrapingException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.FromToDate;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.tournament.AgeCategory;
import cz.cvut.fel.tk21.model.tournament.Gender;
import cz.cvut.fel.tk21.model.tournament.Tournament;
import cz.cvut.fel.tk21.model.tournament.TournamentType;
import cz.cvut.fel.tk21.scraping.service.TournamentScrapingService;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.TournamentService;
import cz.cvut.fel.tk21.service.UserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Component
public class TournamentScraper {

    private static final Logger logger = LoggerFactory.getLogger(TournamentScraper.class);
    private static final String baseUrl = "http://cztenis.cz";
    private static final Map<AgeCategory, String> urls = new HashMap<>();

    private final TournamentService tournamentService;
    private final ClubService clubService;
    private final UserService userService;
    private final TournamentScrapingService tournamentScrapingService;

    public TournamentScraper(TournamentService tournamentService, ClubService clubService, UserService userService, TournamentScrapingService tournamentScrapingService) {
        this.tournamentService = tournamentService;
        this.clubService = clubService;
        this.userService = userService;
        this.tournamentScrapingService = tournamentScrapingService;
        this.createUrlMap();
    }

    private void createUrlMap(){
        urls.put(AgeCategory.YOUNGER, "http://cztenis.cz/mladsi-zactvo/jednotlivci");
        urls.put(AgeCategory.OLDER, "http://cztenis.cz/starsi-zactvo/jednotlivci");
        urls.put(AgeCategory.JUNIOR, "http://cztenis.cz/dorost/jednotlivci");
        urls.put(AgeCategory.ADULTS, "http://cztenis.cz/dospeli/jednotlivci");
    }

    public void updateCurrentTournaments() throws IOException, WebScrapingException {
        logger.trace("Tournament scraping started");

        for (Map.Entry<AgeCategory, String> entry : urls.entrySet()) {
            Document doc = Jsoup.connect(entry.getValue()).get();

            FormElement seasonForm = this.getSeasonForm(doc);
            boolean winter = this.extractIsWinter(seasonForm);
            int year = this.extractYear(seasonForm);

            tournamentScrapingService.deleteTournamentsInYear(year - 3, entry.getKey());

            logger.trace("Scraping tournaments in " + (winter ? "winter" : "summer")  + " year " + year + " category " + entry.getKey().toString());
            findAllTournamentsInDocument(doc, entry.getKey(), year, winter);

            Document nextDoc = getNextDocIfPossible(seasonForm);
            if(nextDoc != null){
                FormElement nextSeasonForm = this.getSeasonForm(nextDoc);
                boolean nextWinter = this.extractIsWinter(nextSeasonForm);
                int nextYear = this.extractYear(nextSeasonForm);

                logger.trace("Scraping tournaments in " + (nextWinter ? "winter" : "summer")  + " year " + nextYear + " category " + entry.getKey().toString());
                findAllTournamentsInDocument(nextDoc, entry.getKey(), nextYear, nextWinter);
            }
        }

        logger.trace("Tournament scraping finished");
    }

    private void findAllTournamentsInDocument(Document doc, AgeCategory ageCategory, int year, boolean winter) throws IOException, WebScrapingException {
        Element tournamentTable = doc.select("table tbody").first();
        assertNonNullElement(tournamentTable, "Tournament Table");
        Elements rows = tournamentTable.select("tr");

        for (Element row : rows){
            Elements cells = row.select("td");

            String dateMen = cells.get(0).html();
            String idMen = cells.get(1).html();
            String clubMen = cells.get(2).html();
            boolean menIsCanceled = cells.get(2).attr("style").equals("text-decoration: line-through;");
            String infoLinkMen = cells.get(3).select("a").attr("href");
            String resultsLinkMen = cells.get(4).select("a").attr("href");
            if(!clubMen.equals("&nbsp;") && !menIsCanceled){
                Tournament tournamentMen = createTournament(dateMen, idMen, clubMen, infoLinkMen, resultsLinkMen, ageCategory, Gender.MALE, year, winter);
                Optional<Tournament> storedOptional = tournamentService.findTournamentByWebId(Long.parseLong(idMen));
                Tournament stored = storedOptional.orElse(null);
                tournamentScrapingService.handleUpdate(tournamentMen, stored);
            }

            String dateWomen = cells.get(6).html();
            String idWomen  = cells.get(7).html();
            String clubWomen  = cells.get(8).html();
            boolean womenIsCanceled = cells.get(8).attr("style").equals("text-decoration: line-through;");
            String infoLinkWomen  = cells.get(9).select("a").attr("href");
            String resultsLinkWomen  = cells.get(10).select("a").attr("href");
            if(!clubWomen.equals("&nbsp;") && !womenIsCanceled){
                Tournament tournamentWomen = createTournament(dateWomen, idWomen, clubWomen, infoLinkWomen, resultsLinkWomen, ageCategory, Gender.FEMALE, year, winter);
                Optional<Tournament> storedOptional = tournamentService.findTournamentByWebId(Long.parseLong(idWomen));
                Tournament stored = storedOptional.orElse(null);
                tournamentScrapingService.handleUpdate(tournamentWomen, stored);
            }
        }
    }

    private Tournament createTournament(String date, String id, String clubName, String infoLink, String resultsLink, AgeCategory ageCategory, Gender gender, int year, boolean winter) throws IOException, WebScrapingException {
        Tournament tournament = new Tournament();
        tournament.setDate(extractDate(date, year, winter));
        tournament.setAgeCategory(ageCategory);
        tournament.setType(extractType(clubName));
        tournament.setGender(gender);
        tournament.setWebId(Long.parseLong(id));
        tournament.setLinkInfo(infoLink.isBlank() ? null : baseUrl + infoLink);
        tournament.setLinkResults(resultsLink.isBlank() ? null : baseUrl + resultsLink);
        tournament.setClub(extractClub(clubName));
        tournament.setPlayers(findAllPlayers(tournament));
        return tournament;
    }

    private List<User> findAllPlayers(Tournament tournament) throws IOException, WebScrapingException {
        List<User> players = new ArrayList<>();
        String link = tournament.getLinkInfo();
        if(link == null) return players;

        Document doc = Jsoup.connect(link).get();

        Elements tables = doc.select("table tbody");
        if(tables.size() < 3) return players;
        Element playerTable = tables.get(2);
        assertNonNullElement(playerTable, "Tournament Table");
        Elements rows = playerTable.select("tr");

        for (Element row : rows) {
            Elements cells = row.select("td");
            if(cells.size() == 1) return players;

            if(cells.get(1).select("a").isEmpty()) continue;
            String playerLink = cells.get(1).select("a").first().attr("href");
            long webId = Long.parseLong(playerLink.split("/")[playerLink.split("/").length - 1]);

            Optional<User> user = userService.findUserByWebId(webId);
            user.ifPresent(players::add);
        }

        return players;
    }

    private FromToDate extractDate(String dateString, int year, boolean winter){
        String fromString = dateString.split("-")[0];
        String toString = dateString.split("-")[1];

        int toDay = Integer.parseInt(toString.split("\\.")[0]);
        int toMonth = Integer.parseInt(toString.split("\\.")[1]);
        int toYear = year;

        String[] fromSplitted = fromString.split("\\.");
        int fromDay = Integer.parseInt(fromSplitted[0]);
        int fromMonth = toMonth;
        if(fromSplitted.length >= 2){
            fromMonth = Integer.parseInt(fromString.split("\\.")[1]);
        }
        int fromYear = year;

        if(winter && fromMonth < 5){
            fromYear++;
            toYear++;
        }
        if(toMonth < fromMonth) toYear++;

        LocalDate from = LocalDate.of(fromYear, fromMonth, fromDay);
        LocalDate to = LocalDate.of(toYear, toMonth, toDay);
        return new FromToDate(from, to);
    }

    private TournamentType extractType(String club){
        char letter = club.charAt(club.length() - 2);
        return TournamentType.getTypeFromCharacter(letter);
    }

    private Club extractClub(String html){
        String s = html;
        if(s.contains("<br>")){
            s = s.split("<br>")[1];
        }
        s = s.substring(0, s.length() - 3);
        String name = s.trim();

        Optional<Club> club = clubService.findClubByName(name);
        if(club.isEmpty()) return null;
        return club.get();
    }

    private FormElement getSeasonForm(Document doc) throws WebScrapingException {
        Element potentialForm = doc.select("form.well").first();
        assertNonNullElement(potentialForm, "Season selector");
        return (FormElement) potentialForm;
    }

    private boolean extractIsWinter(FormElement form) throws WebScrapingException {
        String label = extractSelectedName(form);
        return label.substring(0, 4).equals("zima");
    }

    private int extractYear(FormElement form) throws WebScrapingException {
        String label = extractSelectedName(form);
        String year = label.split(" ")[1].substring(0, 4);
        return Integer.parseInt(year);
    }

    private String extractSelectedName(FormElement form) throws WebScrapingException {
        Element selectSeason = form.select("select").first();
        assertNonNullElement(selectSeason, "Form select");
        Element selected = selectSeason.select("option[selected]").first();
        return selected.html();
    }

    private Document getNextDocIfPossible(FormElement form) throws IOException, WebScrapingException {
        Element selectSeason = form.select("select").first();
        assertNonNullElement(selectSeason, "Form select");
        Elements options = selectSeason.select("option");
        Element selected = selectSeason.select("option[selected]").first();

        if(options.get(0).html().equals(selected.html())) return null;

        options.forEach(o -> o.attr("selected", false));
        Element optionFirst = options.get(0);
        optionFirst.attr("selected", true);

        return form.submit().post();
    }

    private void assertNonNullElement(Element element, String name) throws WebScrapingException {
        if(element == null){
            throw new WebScrapingException("Unable to find element " + name);
        }
    }

}
