package cz.cvut.fel.tk21.rest.dto.user;

import cz.cvut.fel.tk21.rest.dto.teams.CompetitionDto;
import cz.cvut.fel.tk21.rest.dto.tournament.TournamentDto;

import java.util.List;

public class UserCompetitionsDto {

    private List<CompetitionDto> teamCompetitions;

    private List<TournamentDto> tournaments;

    public UserCompetitionsDto(List<CompetitionDto> teamCompetitions, List<TournamentDto> tournaments) {
        this.teamCompetitions = teamCompetitions;
        this.tournaments = tournaments;
    }

    public List<CompetitionDto> getTeamCompetitions() {
        return teamCompetitions;
    }

    public void setTeamCompetitions(List<CompetitionDto> teamCompetitions) {
        this.teamCompetitions = teamCompetitions;
    }

    public List<TournamentDto> getTournaments() {
        return tournaments;
    }

    public void setTournaments(List<TournamentDto> tournaments) {
        this.tournaments = tournaments;
    }
}
