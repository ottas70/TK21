package cz.cvut.fel.tk21.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.tk21.model.mail.ConfirmationToken;
import cz.cvut.fel.tk21.model.teams.Team;
import cz.cvut.fel.tk21.model.tournament.Tournament;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;

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

    @JsonIgnore
    @Basic
    @Column
    private String password;

    @JsonIgnore
    @Basic(optional = false)
    @Column(nullable = false)
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
    @OneToOne(mappedBy = "user")
    private Invitation professionalPlayerInvitation;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private Collection<Post> posts;

    @JsonIgnore
    @ManyToMany(mappedBy = "users", cascade = CascadeType.PERSIST)
    private List<Team> teams;

    @JsonIgnore
    @ManyToMany(mappedBy = "players")
    private List<Tournament> tournaments;

    /*** For Web scraping ***/
    @JsonIgnore
    @Column
    private long webId;

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

    public Invitation getProfessionalPlayerInvitation() {
        return professionalPlayerInvitation;
    }

    public void setProfessionalPlayerInvitation(Invitation professionalPlayerInvitation) {
        this.professionalPlayerInvitation = professionalPlayerInvitation;
    }

    public Collection<Post> getPosts() {
        return posts;
    }

    public void setPosts(Collection<Post> posts) {
        this.posts = posts;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Tournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(List<Tournament> tournaments) {
        this.tournaments = tournaments;
    }

    public long getWebId() {
        return webId;
    }

    public void setWebId(long webId) {
        this.webId = webId;
    }
}
