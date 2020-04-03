package cz.cvut.fel.tk21.rest.dto.teams;

import cz.cvut.fel.tk21.model.teams.Team;

import java.util.List;
import java.util.stream.Collectors;

public class TeamDto {

    private String name;

    private int ranking;

    private int points;

    private int wins;

    private int losses;

    private String link;

    private List<MatchDto> homeMatches;

    private List<MatchDto> awayMatches;

    public TeamDto() {
    }

    public TeamDto(Team team) {
        this.name = team.getName();
        this.ranking = team.getRanking();
        this.points = team.getPoints();
        this.wins = team.getWins();
        this.losses = team.getLosses();
        this.link = team.getLink();
        this.homeMatches = team.getHomeMatches().stream().map(m -> new MatchDto(m, true)).collect(Collectors.toList());
        this.awayMatches = team.getAwayMatches().stream().map(m -> new MatchDto(m, false)).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<MatchDto> getHomeMatches() {
        return homeMatches;
    }

    public void setHomeMatches(List<MatchDto> homeMatches) {
        this.homeMatches = homeMatches;
    }

    public List<MatchDto> getAwayMatches() {
        return awayMatches;
    }

    public void setAwayMatches(List<MatchDto> awayMatches) {
        this.awayMatches = awayMatches;
    }
}
