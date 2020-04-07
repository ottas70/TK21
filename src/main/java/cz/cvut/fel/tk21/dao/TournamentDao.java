package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Post;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.tournament.Tournament;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
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

    public List<Tournament> findAllUpcomingTournamentsForUser(User user){
        return em.createQuery("SELECT t FROM Tournament t " +
                "WHERE :user MEMBER OF t.players", Tournament.class)
                .setParameter("user", user)
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

}
