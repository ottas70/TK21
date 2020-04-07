package cz.cvut.fel.tk21.scraping.scrapers;

import cz.cvut.fel.tk21.exception.WebScrapingException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.scraping.service.PlayerScrapingService;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.ws.dto.PlayerInfoMessageBody;
import cz.cvut.fel.tk21.ws.dto.helperDto.PlayerInfoCzTenis;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class PlayerScraper {

    private static final Logger logger = LoggerFactory.getLogger(PlayerScraper.class);
    private static final String base_url = "http://cztenis.cz/";
    private static final String player_url = "http://cztenis.cz/hrac/";

    private final PlayerScrapingService playerScrapingService;
    private final ClubService clubService;

    @Autowired
    public PlayerScraper(PlayerScrapingService playerScrapingService, ClubService clubService) {
        this.playerScrapingService = playerScrapingService;
        this.clubService = clubService;
    }

    public List<PlayerInfoCzTenis> findPlayersOnCzTenis(PlayerInfoMessageBody details, Club club) throws IOException {
        String playerName = details.getSurname() + " " + details.getName();
        logger.trace(String.format("Looking for player %s on CzTenis", playerName));

        Document doc = Jsoup.connect(base_url).get();

        Document playersDoc = loadPlayersDocumentViaSearchBar(doc, playerName);

        return findPlayersInTable(playersDoc, details, club);
    }

    private Document loadPlayersDocumentViaSearchBar(Document doc, String name) throws IOException {
        Element potentialForm = doc.select("form.navbar-search").first();
        assertNonNullElement(potentialForm, "Player search form");
        FormElement playerForm = (FormElement) potentialForm;

        Element textSearch = playerForm.select("[name$=hledej]").first();
        assertNonNullElement(textSearch, "Search text input");
        textSearch.val(name);

        return playerForm.submit().post();
    }

    private List<PlayerInfoCzTenis> findPlayersInTable(Document doc, PlayerInfoMessageBody detail, Club club){
        List<PlayerInfoCzTenis> results = new ArrayList<>();

        Element playersTable = doc.select("table tbody").first();
        assertNonNullElement(playersTable, "Players Table");
        Elements rows = playersTable.select("tr");

        if(rows.size() == 1 && rows.get(0).select("td").size() == 1){
            return results;
        }

        for(Element row : rows){
            Elements cells = row.select("td");
            String name = cells.get(0).select("a").html();
            String firstName = name.split(" ")[1];
            String lastName = name.split(" ")[0];
            LocalDate birthDate = LocalDate.parse(cells.get(1).html(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String clubName = cells.get(2).html();

            String playersName = detail.getSurname() + " " + detail.getName();
            String playersClubName = club.getName();

            if(name.toLowerCase().equals(playersName.toLowerCase()) && clubName.equals(playersClubName)){
                String href = cells.get(0).select("a").attr("href");
                long id = Long.parseLong(href.split("/")[2]);

                PlayerInfoCzTenis info = new PlayerInfoCzTenis();
                info.setId(id);
                info.setName(firstName);
                info.setSurname(lastName);
                info.setBirthDate(birthDate);
                info.setClubId(club.getId());
                info.setClubName(clubName);
                info.setPlayerEmail(detail.getPlayerEmail());
                info.setLink(player_url + id);
                results.add(info);
            }
        }

        return results;
    }

    public void updatePlayers(List<User> players) throws IOException {
        for (User player : players){
            long webId = player.getWebId();
            User stored = player;
            User found = scrapePlayerDetail(webId);
            playerScrapingService.handleUpdate(found, stored);
        }
    }

    private User scrapePlayerDetail(long webId) throws IOException {
        Document doc = Jsoup.connect(player_url + webId).get();

        String name = doc.select("h2").first().html();
        Element playerTable = doc.select("table tbody").first();
        assertNonNullElement(playerTable, "Players Table");
        Elements rows = playerTable.select("tr");

        String firstName = name.split(" ")[1];
        String lastName = name.split(" ")[0];
        String birthDate = rows.get(0).select("td").get(1).select("strong").first().html();
        String certificateExpiration = rows.get(1).select("td").get(1).select("strong").first().html();
        String clubName = rows.get(2).select("td").get(1).select("strong").first().html();

        Optional<Club> clubOptional = clubService.findClubByName(clubName);
        Club club = clubOptional.orElse(null);

        User found = new User();
        found.setName(firstName);
        found.setSurname(lastName);
        found.setRootClub(club);

        return found;
    }

    private void assertNonNullElement(Element element, String name){
        if(element == null){
            throw new WebScrapingException("Unable to find element " + name);
        }
    }

}
