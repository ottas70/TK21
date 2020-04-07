package cz.cvut.fel.tk21.scraping.service;

import cz.cvut.fel.tk21.model.teams.Match;
import cz.cvut.fel.tk21.model.teams.Team;
import cz.cvut.fel.tk21.model.teams.TeamCompetition;
import cz.cvut.fel.tk21.service.MatchService;
import cz.cvut.fel.tk21.service.TeamCompetitionService;
import cz.cvut.fel.tk21.service.TeamService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TeamCompetitionScrapingService {

    private final TeamCompetitionService teamCompetitionService;
    private final TeamService teamService;
    private final MatchService matchService;

    public TeamCompetitionScrapingService(TeamCompetitionService teamCompetitionService, TeamService teamService, MatchService matchService) {
        this.teamCompetitionService = teamCompetitionService;
        this.teamService = teamService;
        this.matchService = matchService;
    }

    public TeamCompetition handleUpdate(TeamCompetition found, TeamCompetition stored){
        if(found == null && stored == null) return null;
        if(found == null) return stored;
        if(stored == null){
            //Add new competition
            return teamCompetitionService.persist(found);
        } else {
            //Update competition
            stored.setName(found.getName());
            stored.setYear(found.getYear());
            stored.setAgeCategory(found.getAgeCategory());
            stored.setLink(found.getLink());
            stored.setRegion(found.getRegion());
            teamCompetitionService.update(stored);
            return stored;
        }
    }

    public void deleteCompetitions(List<TeamCompetition> toBeDeleted){
        for (TeamCompetition competition : toBeDeleted) {
            deleteAllTeamsAndMatchesInCompetition(competition);
            teamCompetitionService.remove(competition);
        }
    }

    public void deleteAllTeamsAndMatchesInCompetition(TeamCompetition competition){
        List<Team> teams = teamService.findAllTeamsByCompetition(competition);
        for (Team team : teams){
            List<Match> matches = matchService.findMatchesByTeam(team);
            for (Match match : matches){
                matchService.remove(match);
            }
            teamService.remove(team);
        }
    }

}
