package cz.cvut.fel.tk21.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.tk21.model.mail.ConfirmationToken;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

@Entity
@Table(name = "Users",
        indexes = {@Index(name = "email_index", columnList = "email", unique = true)})
@NamedQueries({
        @NamedQuery(name = "Users.getByEmail", query = "select u from User u where u.email=:email")
})
public class User extends AbstractEntity {

    @Basic(optional = false)
    @Column(nullable = false)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Basic(optional = false)
    @Column(nullable = false)
    @NotBlank(message = "Surname is mandatory")
    private String surname;

    @Basic(optional = false)
    @Column(nullable = false, unique = true, name = "email")
    @NotBlank(message = "Email is mandatory")
    @Email
    private String email;

    @Basic(optional = false)
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Basic(optional = false)
    @Column(nullable = false)
    @JsonIgnore
    private boolean verifiedAccount;

    @JsonIgnore
    @OneToOne
    private Club rootClub;

    @JsonIgnore
    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private ConfirmationToken confirmationToken;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Collection<ClubRelation> clubs;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private Collection<Reservation> reservations;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private Collection<VerificationRequest> verificationRequests;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private Collection<Post> posts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isVerifiedAccount() {
        return verifiedAccount;
    }

    public void setVerifiedAccount(boolean verifiedAccount) {
        this.verifiedAccount = verifiedAccount;
    }

    public Club getRootClub() {
        return rootClub;
    }

    public void setRootClub(Club rootClub) {
        this.rootClub = rootClub;
    }

    public ConfirmationToken getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(ConfirmationToken confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public Collection<ClubRelation> getClubs() {
        return clubs;
    }

    public void setClubs(Collection<ClubRelation> clubs) {
        this.clubs = clubs;
    }

    public Collection<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Collection<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Collection<VerificationRequest> getVerificationRequests() {
        return verificationRequests;
    }

    public void setVerificationRequests(Collection<VerificationRequest> verificationRequests) {
        this.verificationRequests = verificationRequests;
    }

    public Collection<Post> getPosts() {
        return posts;
    }

    public void setPosts(Collection<Post> posts) {
        this.posts = posts;
    }
}
