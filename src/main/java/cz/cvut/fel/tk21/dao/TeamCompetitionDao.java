package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.teams.TeamCompetition;
import cz.cvut.fel.tk21.model.tournament.AgeCategory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TeamCompetitionDao extends BaseDao<TeamCompetition> {

    protected TeamCompetitionDao() {
        super(TeamCompetition.class);
    }

    public List<TeamCompetition> findAllCompetitionsInYear(int year){
        return em.createQuery("SELECT c FROM TeamCompetition c " +
                "WHERE c.year = :year", TeamCompetition.class)
                .setParameter("year", year)
                .getResultList();
    }

    public List<TeamCompetition> findAllCompetitionsInYearAndCategory(int year, AgeCategory ageCategory){
        return em.createQuery("SELECT c FROM TeamCompetition c " +
                "WHERE c.year = :year AND c.ageCategory = :ageCategory", TeamCompetition.class)
                .setParameter("year", year)
                .setParameter("ageCategory", ageCategory)
                .getResultList();
    }

}
