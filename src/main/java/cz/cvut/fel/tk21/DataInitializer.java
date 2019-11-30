package cz.cvut.fel.tk21;

import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(ApplicationArguments args) {
        User user1 = new User();
        user1.setName("Otto");
        user1.setSurname("Vodvářka");
        user1.setEmail("ottas70@gmail.com");
        user1.setPassword("$2a$10$IElljSAqagcee0twmltgxOenM5m45VL7fu.kuWCXadl5XVBVVO7Qu"); //abcd
        user1.setVerifiedAccount(true);
        userService.persist(user1);

        User user2 = new User();
        user2.setName("Ondřej");
        user2.setSurname("Mareš");
        user2.setEmail("ondramares@ondramares.com");
        user2.setPassword("$2a$10$IElljSAqagcee0twmltgxOenM5m45VL7fu.kuWCXadl5XVBVVO7Qu"); //abcd
        user2.setVerifiedAccount(true);
        userService.persist(user2);
    }

}
