package cz.cvut.fel.tk21.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class VerificationRequest extends AbstractEntity {

    @ManyToOne
    private User user;

    @ManyToOne
    private Club club;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column
    private boolean accepted;

    @Column
    private boolean denied;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isDenied() {
        return denied;
    }

    public void setDenied(boolean denied) {
        this.denied = denied;
    }
}
