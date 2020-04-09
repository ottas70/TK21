package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Post;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.tournament.AgeCategory;
import cz.cvut.fel.tk21.model.tournament.Tournament;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Repository
public class TournamentDao extends BaseDao<Tournament>{

    protected TournamentDao() {
        super(Tournament.class);
    }

    public List<Tournament> findTournamentsByClub(Club club){
        return em.createQuery("SELECT t FROM Tournament t " +
                "WHERE t.club = :club", Tournament.class)
                .setParameter("club", club)
                .getResultList();
    }

    public List<Tournament> findTournamentsByUser(User user){
        return em.createQuery("SELECT t FROM Tournament t " +
                "WHERE :user MEMBER OF t.players", Tournament.class)
                .setParameter("user", user)
                .getResultList();
    }

    public List<Tournament> findAllUpcomingTournamentsForUser(User user){
        LocalDate now = LocalDate.now();
        return em.createQuery("SELECT t FROM Tournament t " +
                "WHERE :user MEMBER OF t.players AND t.date.from >= :now", Tournament.class)
                .setParameter("user", user)
                .setParameter("now", now)
                .getResultList();
    }

    public Optional<Tournament> findTournamentByWebId(long webId) {
        try{
            return Optional.ofNullable(
                    em.createQuery("SELECT t From Tournament t " +
                            "WHERE t.webId = :webId", Tournament.class)
                            .setParameter("webId", webId)
                            .getSingleResult()
            );
        } catch (NoResultException ex){
            return Optional.empty();
        }
    }

    public List<Tournament> findTournamentsByYearAndCategory(int year, AgeCategory category){
        LocalDate start = LocalDate.parse("01-01-" + year, DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        LocalDate end = LocalDate.parse("12-31-" + year, DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        return em.createQuery("SELECT t FROM Tournament t " +
                "WHERE t.date.from >= :start AND t.date.from <= :end " +
                "AND t.ageCategory = :category", Tournament.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("category", category)
                .getResultList();
    }

}
