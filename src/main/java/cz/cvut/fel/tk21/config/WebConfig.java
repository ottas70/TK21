package cz.cvut.fel.tk21.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    //TODO change in future
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("http://www.book-software.com");
    }
}
