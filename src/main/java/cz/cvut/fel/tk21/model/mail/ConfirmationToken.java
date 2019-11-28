package cz.cvut.fel.tk21.model.mail;

import cz.cvut.fel.tk21.model.AbstractEntity;
import cz.cvut.fel.tk21.model.User;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Entity
public class ConfirmationToken extends AbstractEntity {

    @Column(nullable = false)
    private String confirmationToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToOne(mappedBy = "confirmationToken")
    private User user;

    public ConfirmationToken() {
    }

    public ConfirmationToken(User user) {
        this.user = user;
        this.createdAt = new Date();
        confirmationToken = UUID.randomUUID().toString();
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedDate(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isValid(){
        Date current = new Date();
        long diffInMillies = Math.abs(current.getTime() - createdAt.getTime());
        long diff = TimeUnit.HOURS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return diff <= 24;
    }

}
