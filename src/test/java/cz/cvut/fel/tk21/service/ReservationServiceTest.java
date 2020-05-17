package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.environment.Environment;
import cz.cvut.fel.tk21.environment.Generator;
import cz.cvut.fel.tk21.model.Reservation;
import cz.cvut.fel.tk21.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class ReservationServiceTest {

    @PersistenceContext
    private EntityManager em;
    private final ReservationService service;

    @Autowired
    ReservationServiceTest(ReservationService service) {
        this.service = service;
    }

    @Test
    public void isMine_MineReservation_True() {
        // arrange
        User user = Generator.generateUser();
        Reservation reservation = Generator.generateReservation();
        reservation.setUser(user);
        em.persist(user);
        em.persist(reservation);
        Environment.setCurrentUser(user);

        // act
        boolean isMine = service.isMine(reservation);

        // assert
        assertTrue(isMine);
    }

    @Test
    public void isMine_AnonymousUser_False() {
        // arrange
        User user = Generator.generateUser();
        Reservation reservation = Generator.generateReservation();
        reservation.setUser(user);
        em.persist(user);
        em.persist(reservation);
        Environment.setAnonymousUser();

        // act
        boolean isMine = service.isMine(reservation);

        // assert
        assertFalse(isMine);
    }

    @Test
    public void isMine_DifferentUserReservation_False() {
        // arrange
        User user = Generator.generateUser();
        User user2 = Generator.generateUser();
        Reservation reservation = Generator.generateReservation();
        reservation.setUser(user2);
        em.persist(user);
        em.persist(user2);
        em.persist(reservation);
        Environment.setCurrentUser(user);

        // act
        boolean isMine = service.isMine(reservation);

        // assert
        assertFalse(isMine);
    }
}