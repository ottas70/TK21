package cz.cvut.fel.tk21.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name = "ClubRelation")
public class ClubRelation extends AbstractEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    Collection<UserRole> roles = new HashSet<>();

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

    public void addRole(UserRole role){
        roles.add(role);
    }

    public void removeRole(UserRole role){
        roles.removeIf(r -> r.name().equals(role.name()));
    }
}
