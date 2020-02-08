package cz.cvut.fel.tk21.scraping;

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

    @Scheduled(cron = "0 45 12 * * *")
    public void scrapeCzTenis(){
        try {
            System.out.println("Started scraping");
            clubScraper.findAllClubs();
            System.out.println("Finished scraping");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
