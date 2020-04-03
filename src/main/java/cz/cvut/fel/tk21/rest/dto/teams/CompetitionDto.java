package cz.cvut.fel.tk21.rest.dto.teams;

import cz.cvut.fel.tk21.model.teams.Team;
import cz.cvut.fel.tk21.model.teams.TeamCompetition;
import cz.cvut.fel.tk21.model.tournament.AgeCategory;

import java.util.List;
import java.util.stream.Collectors;

public class CompetitionDto {

    private String name;

    private int year;

    private AgeCategory ageCategory;

    private String link;

    private List<TeamDto> teams;

    public CompetitionDto() {
    }

    public CompetitionDto(TeamCompetition competition, List<Team> teams) {
        this.name = competition.getName();
        this.year = competition.getYear();
        this.ageCategory = competition.getAgeCategory();
        this.link = competition.getLink();
        this.teams = teams.stream().map(TeamDto::new).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public AgeCategory getAgeCategory() {
        return ageCategory;
    }

    public void setAgeCategory(AgeCategory ageCategory) {
        this.ageCategory = ageCategory;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<TeamDto> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamDto> teams) {
        this.teams = teams;
    }
}
