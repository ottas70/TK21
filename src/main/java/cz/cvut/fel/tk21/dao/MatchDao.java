package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Post;
import cz.cvut.fel.tk21.model.teams.Match;
import cz.cvut.fel.tk21.model.teams.Team;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MatchDao extends BaseDao<Match> {

    protected MatchDao() {
        super(Match.class);
    }

    public List<Match> findHomeMatchesByTeam(Team team){
        return em.createQuery("SELECT m FROM Match m " +
                "WHERE m.homeTeam = :team", Match.class)
                .setParameter("team", team)
                .getResultList();
    }

    public List<Match> findAwayMatchesByTeam(Team team){
        return em.createQuery("SELECT m FROM Match m " +
                "WHERE m.awayTeam = :team", Match.class)
                .setParameter("team", team)
                .getResultList();
    }

}
