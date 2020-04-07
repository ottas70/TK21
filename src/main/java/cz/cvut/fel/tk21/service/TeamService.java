package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.TeamDao;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.teams.Team;
import cz.cvut.fel.tk21.model.teams.TeamCompetition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService extends BaseService<TeamDao, Team>{

    protected TeamService(TeamDao dao) {
        super(dao);
    }

    @Transactional(readOnly = true)
    public List<Team> findAllTeamsInYearByClub(Club club, int year){
        return dao.findAllTeamsInYearByClub(club, year);
    }

    @Transactional(readOnly = true)
    public List<Team> findAllTeamsInYearByUser(User user, int year){
        return dao.findAllTeamsInYearByUser(user, year);
    }

    @Transactional(readOnly = true)
    public List<Team> findAllTeamsByCompetition(TeamCompetition teamCompetition){
        return dao.findAllTeamsByCompetition(teamCompetition);
    }

}
