package cz.cvut.fel.tk21.model.teams;

import cz.cvut.fel.tk21.model.AbstractEntity;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team extends AbstractEntity {

    @Column
    private String name;

    @ManyToOne
    private TeamCompetition competition;

    @OneToMany(mappedBy = "homeTeam")
    private List<Match> homeMatches = new ArrayList<>();

    @OneToMany(mappedBy = "awayTeam")
    private List<Match> awayMatches = new ArrayList<>();

    @ManyToMany
    private List<User> users = new ArrayList<>();

    @ManyToOne
    private Club club;

    @Column
    private int ranking;

    @Column
    private int wins;

    @Column
    private int losses;

    @Column
    private int points;

    @Column
    private String link;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeamCompetition getCompetition() {
        return competition;
    }

    public void setCompetition(TeamCompetition competition) {
        this.competition = competition;
    }

    public List<Match> getHomeMatches() {
        return homeMatches;
    }

    public void setHomeMatches(List<Match> homeMatches) {
        this.homeMatches = homeMatches;
    }

    public List<Match> getAwayMatches() {
        return awayMatches;
    }

    public void setAwayMatches(List<Match> awayMatches) {
        this.awayMatches = awayMatches;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void addUser(User user){
        users.add(user);
    }

    public void removeUser(User user){
        users.removeIf(u -> u.getId() == user.getId());
    }

}
