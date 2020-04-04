package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.MatchDao;
import cz.cvut.fel.tk21.model.teams.Match;
import cz.cvut.fel.tk21.model.teams.Team;
import cz.cvut.fel.tk21.model.teams.TeamCompetition;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService extends BaseService<MatchDao, Match> {

    protected MatchService(MatchDao dao) {
        super(dao);
    }

    public List<Match> findHomeMatchesByTeam(Team team){
        return dao.findHomeMatchesByTeam(team);
    }

    public List<Match> findAwayMatchesByTeam(Team team){
        return dao.findAwayMatchesByTeam(team);
    }

}
