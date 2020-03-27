package cz.cvut.fel.tk21.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Invitation extends AbstractEntity{

    @Column(nullable = false)
    private String confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column
    private long webId;

    @OneToOne
    private User user;

    @ManyToOne
    private Club club;

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getWebId() {
        return webId;
    }

    public void setWebId(long webId) {
        this.webId = webId;
    }

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
}
