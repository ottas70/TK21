package cz.cvut.fel.tk21.scraping;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.scraping.scrapers.ClubScraper;
import cz.cvut.fel.tk21.scraping.scrapers.PlayerScraper;
import cz.cvut.fel.tk21.scraping.scrapers.TeamCompetitionScraper;
import cz.cvut.fel.tk21.scraping.scrapers.TournamentScraper;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class WebScraper {

    private static final Logger log = LoggerFactory.getLogger(WebScraper.class);

    private final ClubScraper clubScraper;
    private final TournamentScraper tournamentScraper;
    private final TeamCompetitionScraper teamCompetitionScraper;
    private final PlayerScraper playerScraper;

    private final ClubService clubService;
    private final UserService userService;

    @Autowired
    public WebScraper(ClubScraper clubScraper, TournamentScraper tournamentScraper, TeamCompetitionScraper teamCompetitionScraper, PlayerScraper playerScraper, ClubService clubService, UserService userService) {
        this.clubScraper = clubScraper;
        this.tournamentScraper = tournamentScraper;
        this.teamCompetitionScraper = teamCompetitionScraper;
        this.playerScraper = playerScraper;
        this.clubService = clubService;
        this.userService = userService;
    }

    //TODO configure this
    //@Scheduled(cron = "0 25 12 * * *")
    public void scrapeCzTenis(){
        try {
            scrapeClubs();
            scrapePlayers();
            scrapeTournaments();
            scrapeTeamCompetitions();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void scrapeClubs() throws IOException{
        List<Club> toBeFound = clubService.findAllScrapedClubs();
        clubScraper.updateClubs(toBeFound);
    }

    private void scrapePlayers() throws IOException {
        List<User> toBeFound = userService.findAllScrapedPlayers();
        playerScraper.updatePlayers(toBeFound);
    }

    private void scrapeTournaments() throws IOException {
        tournamentScraper.updateCurrentTournaments();
    }

    private void scrapeTeamCompetitions() throws IOException {
        teamCompetitionScraper.updateAllCompetitions();
    }

}
