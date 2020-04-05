package cz.cvut.fel.tk21;

import cz.cvut.fel.tk21.model.*;
import cz.cvut.fel.tk21.scraping.scrapers.ClubScraper;
import cz.cvut.fel.tk21.scraping.scrapers.PlayerScraper;
import cz.cvut.fel.tk21.scraping.scrapers.TeamCompetitionScraper;
import cz.cvut.fel.tk21.scraping.scrapers.TournamentScraper;
import cz.cvut.fel.tk21.service.ClubRelationService;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.ws.dto.PlayerInfoMessageBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserService userService;
    private final ClubService clubService;
    private final ClubRelationService clubRelationService;
    private final Random random;

    @Autowired
    private ClubScraper clubScraper;
    @Autowired
    private PlayerScraper playerScraper;
    @Autowired
    private TournamentScraper tournamentScraper;
    @Autowired
    private TeamCompetitionScraper teamCompetitionScraper;

    public DataInitializer(UserService userService, ClubService clubService, ClubRelationService clubRelationService, Random random) {
        this.userService = userService;
        this.clubService = clubService;
        this.clubRelationService = clubRelationService;
        this.random = random;
    }


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

        User user3 = new User();
        user3.setName("Tenis");
        user3.setSurname("Březno TEST");
        user3.setEmail("tenis.brezno@hotmail.cz");
        user3.setPassword("$2a$10$IElljSAqagcee0twmltgxOenM5m45VL7fu.kuWCXadl5XVBVVO7Qu"); //abcd
        user3.setVerifiedAccount(true);
        userService.persist(user3);

        User user4 = new User();
        user4.setName("Tenis");
        user4.setSurname("Neride TEST");
        user4.setEmail("tkneride@seznam.cz");
        user4.setPassword("$2a$10$IElljSAqagcee0twmltgxOenM5m45VL7fu.kuWCXadl5XVBVVO7Qu"); //abcd
        user4.setVerifiedAccount(true);
        userService.persist(user4);

        Club club = new Club();
        club.setId(1);
        club.setName("Test TK Neridé");
        club.setDescription("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Nulla non arcu lacinia neque faucibus fringilla. Mauris metus. Maecenas fermentum, sem in pharetra pellentesque, velit turpis volutpat ante, in pharetra metus odio a lectus. Nullam sit amet magna in magna gravida vehicula. Aliquam erat volutpat. Etiam commodo dui eget wisi. Phasellus faucibus molestie nisl. Duis sapien nunc, commodo et, interdum suscipit, sollicitudin et, dolor. Morbi leo mi, nonummy eget tristique non, rhoncus non leo. Nulla quis diam.");
        club.setTelephone("723598253");
        club.addEmail("neride@gmail.com");
        club.setWeb("neride.cz");
        Address address = new Address();
        address.setStreet("V Chotejně 24");
        address.setCity("Praha");
        address.setZip("123 00");
        club.setAddress(address);
        club.setOpeningHours(getInitialOpeningHours());
        club.setReservationPermission(ReservationPermission.SIGNED);
        club.setMinReservationTime(15);
        club.setMaxReservationTime(180);
        club.addCourt(getRandomTennisCourt(1));
        club.addCourt(getRandomTennisCourt(2));
        club.addCourt(getRandomTennisCourt(3));
        setInitialSeasons(club);
        club.setRegistered(true);
        clubService.persist(club);

        ClubRelation relation = new ClubRelation();
        relation.setClub(club);
        relation.setUser(user1);
        relation.addRole(UserRole.ADMIN);
        relation.addRole(UserRole.RECREATIONAL_PLAYER);
        clubRelationService.persist(relation);

        for (int i = 1; i <= 3; i++) {
            addNumberedUserInClub(i, club);
        }

        Club club2 = new Club();
        club2.setId(3);
        club2.setName("Test Tk Písnice");
        club2.setDescription("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Nulla non arcu lacinia neque faucibus fringilla. Mauris metus. Maecenas fermentum, sem in pharetra pellentesque, velit turpis volutpat ante, in pharetra metus odio a lectus. Nullam sit amet magna in magna gravida vehicula. Aliquam erat volutpat. Etiam commodo dui eget wisi. Phasellus faucibus molestie nisl. Duis sapien nunc, commodo et, interdum suscipit, sollicitudin et, dolor. Morbi leo mi, nonummy eget tristique non, rhoncus non leo. Nulla quis diam.");
        club2.setTelephone("749724167");
        club2.addEmail("pisnice@gmail.com");
        club2.setWeb("pisnice.cz");
        Address address2 = new Address();
        address2.setStreet("Ve Dvorcích 12");
        address2.setCity("Praha");
        address2.setZip("150 00");
        club2.setAddress(address2);
        club2.setOpeningHours(getInitialOpeningHours());
        club2.setReservationPermission(ReservationPermission.SIGNED);
        club2.setMinReservationTime(15);
        club2.setMaxReservationTime(180);
        club2.addCourt(getRandomTennisCourt(4));
        club2.addCourt(getRandomTennisCourt(5));
        club2.addCourt(getRandomTennisCourt(6));
        setInitialSeasons(club2);
        club2.setRegistered(true);
        clubService.persist(club2);

        ClubRelation relation2 = new ClubRelation();
        relation2.setClub(club2);
        relation2.setUser(user2);
        relation2.addRole(UserRole.ADMIN);
        clubRelationService.persist(relation2);

        for (int i = 4; i <= 6; i++) {
            addNumberedUserInClub(i, club2);
        }

        //Users with no club
        for (int i = 7; i <= 9; i++) {
            addNumberedUserInClub(i, null);
        }

        try {
            //clubScraper.findAllClubs();
            //tournamentScraper.findAllTournaments();
            //teamCompetitionScraper.findAllCompetitions();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private OpeningHours getInitialOpeningHours(){
        OpeningHours openingHours = new OpeningHours();

        Map<Day, FromToTime> hoursMap = new HashMap<>();
        hoursMap.put(Day.MONDAY, new FromToTime("09:00", "21:00"));
        hoursMap.put(Day.TUESDAY, new FromToTime("07:30", "23:59"));
        hoursMap.put(Day.WEDNESDAY, new FromToTime("00:00", "14:30"));
        hoursMap.put(Day.THURSDAY, new FromToTime("09:30", "18:00"));
        hoursMap.put(Day.FRIDAY, new FromToTime("09:00", "21:30"));
        hoursMap.put(Day.SATURDAY, new FromToTime("12:00", "15:00"));
        hoursMap.put(Day.SUNDAY, new FromToTime());
        openingHours.setRegularHours(hoursMap);

        Map<LocalDate, FromToTime> specialHours = new HashMap<>();
        specialHours.put(LocalDate.parse("12-05-2019", DateTimeFormatter.ofPattern("MM-dd-yyyy")), new FromToTime("11:00", "16:00"));
        specialHours.put(LocalDate.parse("12-24-2019", DateTimeFormatter.ofPattern("MM-dd-yyyy")), new FromToTime());

        openingHours.setSpecialDays(specialHours);

        return openingHours;
    }

    private void setInitialSeasons(Club club){
        Map<Integer, Season> seasons = new HashMap<>();

        Season season2019 = new Season(new FromToDate("03-01-2019", "09-30-2019"), new FromToDate("10-01-2019", "02-28-2020"));
        Season season2020 = new Season(new FromToDate("03-01-2020", "09-30-2020"), new FromToDate("10-01-2020", "02-28-2021"));
        seasons.put(2019, season2019);
        seasons.put(2020, season2020);

        club.setSeasons(seasons);
    }

    private TennisCourt getRandomTennisCourt(int id){
        TennisCourt court = new TennisCourt();
        court.setId(id);
        court.setName("Court" + random.nextInt(100));
        court.setSurfaceType(random.nextInt(100) % 2 == 0 ? SurfaceType.CLAY : SurfaceType.GRASS);
        court.setAvailableInSummer(random.nextInt(100) % 2 == 0);
        court.setAvailableInWinter(random.nextInt(100) % 2 == 0);
        return court;
    }

    private void addNumberedUserInClub(int number, Club club){
        User user = new User();
        user.setName("Some");
        user.setSurname("User" + number);
        user.setEmail("user" + number + "@gmail.com");
        user.setPassword("$2a$10$IElljSAqagcee0twmltgxOenM5m45VL7fu.kuWCXadl5XVBVVO7Qu"); //abcd
        user.setVerifiedAccount(true);
        userService.persist(user);

        if(club != null){
            ClubRelation relation = new ClubRelation();
            relation.setClub(club);
            relation.setUser(user);
            relation.addRole(UserRole.RECREATIONAL_PLAYER);
            clubRelationService.persist(relation);
        }
    }

}
