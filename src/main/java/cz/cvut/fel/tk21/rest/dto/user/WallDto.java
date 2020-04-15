package cz.cvut.fel.tk21.rest.dto.user;

import cz.cvut.fel.tk21.rest.dto.club.ClubRelationshipDto;
import cz.cvut.fel.tk21.rest.dto.reservation.ReservationDto;
import cz.cvut.fel.tk21.rest.dto.teams.CompetitionDto;
import cz.cvut.fel.tk21.rest.dto.tournament.TournamentDto;

import java.util.List;

public class WallDto {

    private List<CompetitionDto> teamCompetitions;

    private List<TournamentDto> tournaments;

    private List<ClubRelationshipDto> clubs;

    private List<ReservationDto> reservations;

    public WallDto(List<CompetitionDto> teamCompetitions, List<TournamentDto> tournaments, List<ClubRelationshipDto> clubs, List<ReservationDto> reservations) {
        this.teamCompetitions = teamCompetitions;
        this.tournaments = tournaments;
        this.clubs = clubs;
        this.reservations = reservations;
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

    public List<ClubRelationshipDto> getClubs() {
        return clubs;
    }

    public void setClubs(List<ClubRelationshipDto> clubs) {
        this.clubs = clubs;
    }

    public List<ReservationDto> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationDto> reservations) {
        this.reservations = reservations;
    }
}
