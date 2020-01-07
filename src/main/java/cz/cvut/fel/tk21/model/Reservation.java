package cz.cvut.fel.tk21.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDate;

@Entity
public class Reservation extends AbstractEntity {

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private FromToTime fromToTime;

    @ManyToOne(optional = false)
    private TennisCourt tennisCourt;

    @ManyToOne
    private User user;

    // for non-registered users
    @Column
    private String email;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public FromToTime getFromToTime() {
        return fromToTime;
    }

    public void setFromToTime(FromToTime fromToTime) {
        this.fromToTime = fromToTime;
    }

    public TennisCourt getTennisCourt() {
        return tennisCourt;
    }

    public void setTennisCourt(TennisCourt tennisCourt) {
        this.tennisCourt = tennisCourt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Club getClub(){
        return tennisCourt.getClub();
    }

    public boolean isForRegisteredUser(){
        return user != null;
    }
}
