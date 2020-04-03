package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.teams.TeamCompetition;
import org.springframework.stereotype.Repository;

@Repository
public class TeamCompetitionDao extends BaseDao<TeamCompetition> {

    protected TeamCompetitionDao() {
        super(TeamCompetition.class);
    }

}
