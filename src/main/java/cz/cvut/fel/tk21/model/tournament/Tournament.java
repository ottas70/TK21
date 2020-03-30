package cz.cvut.fel.tk21.model.tournament;

import cz.cvut.fel.tk21.model.AbstractEntity;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.FromToDate;

import javax.persistence.*;

@Entity
public class Tournament extends AbstractEntity {

    @Embedded
    private FromToDate date;

    @Column
    @Enumerated(EnumType.STRING)
    private AgeCategory ageCategory;

    @Column
    @Enumerated(EnumType.STRING)
    private TournamentType type;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private long webId;

    @Column
    private String linkInfo;

    @Column
    private String linkResults;

    @ManyToOne
    private Club club;

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

    public long getWebId() {
        return webId;
    }

    public void setWebId(long webId) {
        this.webId = webId;
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

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }
}
