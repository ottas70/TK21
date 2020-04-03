package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.teams.Match;
import org.springframework.stereotype.Repository;

@Repository
public class MatchDao extends BaseDao<Match> {

    protected MatchDao() {
        super(Match.class);
    }

}
