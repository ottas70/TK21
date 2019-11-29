package cz.cvut.fel.tk21.model;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "Club_Relation")
public class Club_Relation extends AbstractEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    Collection<UserRole> roles;

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Collection<UserRole> roles) {
        this.roles = roles;
    }
}
