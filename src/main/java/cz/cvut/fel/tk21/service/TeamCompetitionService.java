package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.TeamCompetitionDao;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.teams.Team;
import cz.cvut.fel.tk21.model.teams.TeamCompetition;
import cz.cvut.fel.tk21.rest.dto.teams.CompetitionDto;
import cz.cvut.fel.tk21.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeamCompetitionService extends BaseService<TeamCompetitionDao, TeamCompetition> {

    private final TeamService teamService;

    @Autowired
    protected TeamCompetitionService(TeamCompetitionDao dao, TeamService teamService) {
        super(dao);
        this.teamService = teamService;
    }

    public List<CompetitionDto> getAllTeamCompetitionsInCurrentYear(Club club){
        int year = DateUtils.getCurrentYear();
        List<Team> teams = teamService.findAllTeamsInYearByClub(club, year);
        Map<TeamCompetition, List<Team>> map = new HashMap<>();
        for (Team t : teams){
            map.put(t.getCompetition(), new ArrayList<>());
        }
        for (Team t : teams){
            map.get(t.getCompetition()).add(t);
        }

        List<CompetitionDto> result = new ArrayList<>();
        for (Map.Entry<TeamCompetition, List<Team>> entry : map.entrySet()) {
           result.add(new CompetitionDto(entry.getKey(), entry.getValue()));
        }

        return result;
    }

}
