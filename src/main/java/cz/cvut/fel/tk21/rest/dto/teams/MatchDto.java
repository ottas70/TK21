package cz.cvut.fel.tk21.rest.dto.teams;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.cvut.fel.tk21.model.teams.Match;

import javax.persistence.Column;
import java.time.LocalDate;

public class MatchDto {

    private String opponent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    private LocalDate date;

    private int round;

    private int homePoints;

    private int awayPoints;

    private String detailLink;

    public MatchDto() {
    }

    public MatchDto(Match match, boolean homeMatch) {
        if(homeMatch){
            this.opponent = match.getAwayTeam().getName();
        } else {
            this.opponent = match.getHomeTeam().getName();
        }
        this.date = match.getDate();
        this.round = match.getRound();
        this.homePoints = match.getHomePoints();
        this.awayPoints = match.getAwayPoints();
        this.detailLink = match.getDetailLink();
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
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
