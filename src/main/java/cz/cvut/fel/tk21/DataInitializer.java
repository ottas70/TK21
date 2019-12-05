package cz.cvut.fel.tk21;

import cz.cvut.fel.tk21.model.Address;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private ClubService clubService;

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

        Club club = new Club();
        club.setId(1);
        club.setName("Tk Neride");
        Address address = new Address();
        address.setStreet("V Chotejně 24");
        address.setCity("Praha");
        address.setZip("123 00");
        club.setAddress(address);
        clubService.persist(club);

        Club club2 = new Club();
        club.setId(3);
        club2.setName("Tk Písnice");
        Address address2 = new Address();
        address2.setStreet("Ve Dvorcích 12");
        address2.setCity("Praha");
        address2.setZip("150 00");
        club2.setAddress(address);
        clubService.persist(club2);
    }

}
