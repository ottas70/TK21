package cz.cvut.fel.tk21.model.teams;

import cz.cvut.fel.tk21.model.AbstractEntity;
import cz.cvut.fel.tk21.model.tournament.AgeCategory;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class TeamCompetition extends AbstractEntity {

    @Column
    private String name;

    @Column
    private int year;

    @Column
    @Enumerated(EnumType.STRING)
    private AgeCategory ageCategory;

    @Column
    @Enumerated(EnumType.STRING)
    private Region region;

    @Column
    private int webId;

    @Column
    private String link;

    @OneToMany(mappedBy = "competition")
    private List<Team> teams;

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

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public int getWebId() {
        return webId;
    }

    public void setWebId(int webId) {
        this.webId = webId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeamCompetition)) return false;
        TeamCompetition that = (TeamCompetition) o;
        return this.getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
