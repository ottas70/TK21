package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Post;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.tournament.Tournament;
import org.springframework.stereotype.Repository;

import java.util.List;

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

}
