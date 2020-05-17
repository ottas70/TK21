package cz.cvut.fel.tk21.environment;

import cz.cvut.fel.tk21.model.Address;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Reservation;
import cz.cvut.fel.tk21.model.User;

import java.time.LocalDate;
import java.util.Random;

public class Generator {

    private static final Random random = new Random();

    public static int randomInt() {
        return random.nextInt();
    }

    public static boolean randomBoolean() {
        return random.nextBoolean();
    }

    public static User generateUser() {
        final User user = new User();
        user.setName("FirstName" + randomInt());
        user.setSurname("LastName" + randomInt());
        user.setEmail("username" + randomInt() + "@tk21.cz");
        user.setPassword(Integer.toString(randomInt()));
        return user;
    }

    public static Reservation generateReservation() {
        final Reservation reservation = new Reservation();
        reservation.setDate(LocalDate.now());
        return reservation;
    }

    public static Club generateClub(String name){
        Club club = new Club();
        club.setName(name);
        club.setDescription("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Nulla non arcu lacinia neque faucibus fringilla. Mauris metus. Maecenas fermentum, sem in pharetra pellentesque, velit turpis volutpat ante, in pharetra metus odio a lectus. Nullam sit amet magna in magna gravida vehicula. Aliquam erat volutpat. Etiam commodo dui eget wisi. Phasellus faucibus molestie nisl. Duis sapien nunc, commodo et, interdum suscipit, sollicitudin et, dolor. Morbi leo mi, nonummy eget tristique non, rhoncus non leo. Nulla quis diam.");
        club.setTelephone(Integer.toString(randomInt()));
        club.addEmail("club" + randomInt() + "@tk21.cz");
        Address address = new Address();
        address.setStreet("street" + randomInt());
        address.setCity("city" + randomInt());
        address.setZip(Integer.toString(randomInt()));
        club.setAddress(address);
        return club;
    }

}
