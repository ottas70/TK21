package cz.cvut.fel.tk21.model.teams;

import cz.cvut.fel.tk21.model.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class Match extends AbstractEntity {

    @ManyToOne
    private Team homeTeam;

    @ManyToOne
    private Team awayTeam;

    @Column
    private LocalDate date;

    @Column
    private int round;

    @Column
    private int homePoints;

    @Column
    private int awayPoints;

    @Column
    private String detailLink;

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getHomePoints() {
        return homePoints;
    }

    public void setHomePoints(int homePoints) {
        this.homePoints = homePoints;
    }

    public int getAwayPoints() {
        return awayPoints;
    }

    public void setAwayPoints(int awayPoints) {
        this.awayPoints = awayPoints;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }
}
