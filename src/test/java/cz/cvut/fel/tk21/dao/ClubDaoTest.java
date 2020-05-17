package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.environment.Environment;
import cz.cvut.fel.tk21.environment.Generator;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Reservation;
import cz.cvut.fel.tk21.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class ClubDaoTest {

    @PersistenceContext
    private EntityManager em;
    private final ClubDao dao;

    @Autowired
    ClubDaoTest(ClubDao dao) {
        this.dao = dao;
    }

    @Test
    public void findClubByName_SameName_Found() {
        // arrange
        Club club = Generator.generateClub("TK Písnice");
        em.persist(club);

        // act
        Optional<Club> clubOptional = dao.findClubByName(club.getName());
        Club result = clubOptional.get();

        // assert
        assertEquals(club.getId(), result.getId());
    }
}