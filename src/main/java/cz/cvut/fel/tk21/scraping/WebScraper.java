package cz.cvut.fel.tk21.scraping;

import cz.cvut.fel.tk21.exception.WebScrapingException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.mail.Mail;
import cz.cvut.fel.tk21.scraping.scrapers.ClubScraper;
import cz.cvut.fel.tk21.scraping.scrapers.PlayerScraper;
import cz.cvut.fel.tk21.scraping.scrapers.TeamCompetitionScraper;
import cz.cvut.fel.tk21.scraping.scrapers.TournamentScraper;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.service.mail.MailService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WebScraper {

    private static final Logger log = LoggerFactory.getLogger(WebScraper.class);

    private final ClubScraper clubScraper;
    private final TournamentScraper tournamentScraper;
    private final TeamCompetitionScraper teamCompetitionScraper;
    private final PlayerScraper playerScraper;

    private final ClubService clubService;
    private final UserService userService;
    private final MailService mailService;

    @Autowired
    public WebScraper(ClubScraper clubScraper, TournamentScraper tournamentScraper, TeamCompetitionScraper teamCompetitionScraper, PlayerScraper playerScraper, ClubService clubService, UserService userService, MailService mailService) {
        this.clubScraper = clubScraper;
        this.tournamentScraper = tournamentScraper;
        this.teamCompetitionScraper = teamCompetitionScraper;
        this.playerScraper = playerScraper;
        this.clubService = clubService;
        this.userService = userService;
        this.mailService = mailService;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void scrapeCzTenis(){
        try {
            scrapeClubs();
            scrapePlayers();
            scrapeTournaments();
            scrapeTeamCompetitions();
        } catch (IOException | WebScrapingException | RuntimeException ex) {
            log.error(ex.getMessage());
            sendErrorEmail(ex);
        }
    }

    private void scrapeClubs() throws IOException, WebScrapingException {
        List<Club> toBeFound = clubService.findAllScrapedClubs();
        clubScraper.updateClubs(toBeFound);
    }

    private void scrapePlayers() throws IOException, WebScrapingException {
        List<User> toBeFound = userService.findAllScrapedPlayers();
        playerScraper.updatePlayers(toBeFound);
    }

    private void scrapeTournaments() throws IOException, WebScrapingException {
        tournamentScraper.updateCurrentTournaments();
    }

    private void scrapeTeamCompetitions() throws IOException, WebScrapingException {
        teamCompetitionScraper.updateAllCompetitions();
    }

    private void sendErrorEmail(Exception ex){
        Mail mail = new Mail();
        mail.setFrom("noreply@tk21.cz");
        mail.setTo("ottas70@gmail.com");
        mail.setSubject("TK21 - Chyba při scrapování dne " + LocalDate.now().toString());

        String stacktrace = ExceptionUtils.getStackTrace(ex);

        Map<String, Object> model = new HashMap<>();
        model.put("exception", stacktrace);
        mail.setModel(model);

        mailService.sendScrapingErrorMail(mail);
    }

}
