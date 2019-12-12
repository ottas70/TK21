package cz.cvut.fel.tk21;

import cz.cvut.fel.tk21.model.*;
import cz.cvut.fel.tk21.service.ClubRelationService;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private ClubRelationService clubRelationService;

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
        club.setOpeningHours(getInitialOpeningHours());
        clubService.persist(club);

        ClubRelation relation = new ClubRelation();
        relation.setClub(club);
        relation.setUser(user1);
        relation.addRole(UserRole.ADMIN);
        clubRelationService.persist(relation);

        Club club2 = new Club();
        club2.setId(3);
        club2.setName("Tk Písnice");
        Address address2 = new Address();
        address2.setStreet("Ve Dvorcích 12");
        address2.setCity("Praha");
        address2.setZip("150 00");
        club2.setAddress(address2);
        club2.setOpeningHours(getInitialOpeningHours());
        clubService.persist(club2);

        ClubRelation relation2 = new ClubRelation();
        relation2.setClub(club2);
        relation2.setUser(user2);
        relation2.addRole(UserRole.ADMIN);
        clubRelationService.persist(relation2);
    }

    private OpeningHours getInitialOpeningHours(){
        OpeningHours openingHours = new OpeningHours();

        Map<Day, FromToTime> hoursMap = new HashMap<>();
        hoursMap.put(Day.MONDAY, new FromToTime("09:00", "21:00"));
        hoursMap.put(Day.TUESDAY, new FromToTime("09:00", "21:00"));
        hoursMap.put(Day.WEDNESDAY, new FromToTime("09:00", "21:00"));
        hoursMap.put(Day.THURSDAY, new FromToTime("09:00", "21:00"));
        hoursMap.put(Day.FRIDAY, new FromToTime("09:00", "21:00"));
        hoursMap.put(Day.SATURDAY, new FromToTime("09:00", "21:00"));
        hoursMap.put(Day.SUNDAY, new FromToTime());
        openingHours.setOpeningHours(hoursMap);

        Map<LocalDate, FromToTime> specialHours = new HashMap<>();
        specialHours.put(LocalDate.parse("12-05-2019", DateTimeFormatter.ofPattern("MM-dd-yyyy")), new FromToTime("11:00", "16:00"));
        specialHours.put(LocalDate.parse("12-24-2019", DateTimeFormatter.ofPattern("MM-dd-yyyy")), new FromToTime());

        openingHours.setSpecialDays(specialHours);

        return openingHours;
    }

}
