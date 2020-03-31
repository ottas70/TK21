package cz.cvut.fel.tk21.rest.dto.tournament;

import cz.cvut.fel.tk21.model.FromToDate;
import cz.cvut.fel.tk21.model.tournament.AgeCategory;
import cz.cvut.fel.tk21.model.tournament.Gender;
import cz.cvut.fel.tk21.model.tournament.Tournament;
import cz.cvut.fel.tk21.model.tournament.TournamentType;

public class TournamentDto {

    private int id;

    private FromToDate date;

    private AgeCategory ageCategory;

    private TournamentType type;

    private Gender gender;

    private String linkInfo;

    private String linkResults;

    public TournamentDto() {
    }

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getId();
        this.date = tournament.getDate();
        this.ageCategory = tournament.getAgeCategory();
        this.type = tournament.getType();
        this.gender = tournament.getGender();
        this.linkInfo = tournament.getLinkInfo();
        this.linkResults = tournament.getLinkResults();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FromToDate getDate() {
        return date;
    }

    public void setDate(FromToDate date) {
        this.date = date;
    }

    public AgeCategory getAgeCategory() {
        return ageCategory;
    }

    public void setAgeCategory(AgeCategory ageCategory) {
        this.ageCategory = ageCategory;
    }

    public TournamentType getType() {
        return type;
    }

    public void setType(TournamentType type) {
        this.type = type;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getLinkInfo() {
        return linkInfo;
    }

    public void setLinkInfo(String linkInfo) {
        this.linkInfo = linkInfo;
    }

    public String getLinkResults() {
        return linkResults;
    }

    public void setLinkResults(String linkResults) {
        this.linkResults = linkResults;
    }
}
