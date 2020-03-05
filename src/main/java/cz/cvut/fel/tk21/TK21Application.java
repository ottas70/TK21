package cz.cvut.fel.tk21;

import com.tinify.Tinify;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class TK21Application {

    @Value("${tinify.key}")
    private String tinifyKey;

    @PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));
        Tinify.setKey(tinifyKey);
    }

    public static void main(String[] args) {
        SpringApplication.run(TK21Application.class, args);
    }

}

