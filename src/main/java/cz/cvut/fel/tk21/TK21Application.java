package cz.cvut.fel.tk21;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class TK21Application {

    @PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));
    }

    public static void main(String[] args) {
        SpringApplication.run(TK21Application.class, args);
    }

}

