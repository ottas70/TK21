package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Post;
import cz.cvut.fel.tk21.model.teams.Team;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeamDao extends BaseDao<Team> {

    protected TeamDao() {
        super(Team.class);
    }

    public List<Team> findAllTeamsInYearByClub(Club club, int year){
        return em.createQuery("SELECT t FROM Team t " +
                "WHERE t.club = :club AND t.competition.year = :year", Team.class)
                .setParameter("club", club)
                .setParameter("year", year)
                .getResultList();
    }

}
