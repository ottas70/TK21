package cz.cvut.fel.tk21.scraping;

import cz.cvut.fel.tk21.scraping.scrapers.ClubScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WebScraper {

    private static final Logger log = LoggerFactory.getLogger(WebScraper.class);

    @Autowired
    private ClubScraper clubScraper;

    //TODO configure this
    //@Scheduled(cron = "0 25 12 * * *")
    public void scrapeCzTenis(){
        try {
            clubScraper.findAllClubs();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
