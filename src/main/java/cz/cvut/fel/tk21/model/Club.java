package cz.cvut.fel.tk21.model;

import cz.cvut.fel.tk21.util.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Entity
@Table(name = "Club")
public class Club extends AbstractEntity {

    @Column(nullable = false)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Column(nullable = false)
    private String nameSearch;

    @Column
    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    @Embedded
    private Address address;

    @Temporal(TemporalType.DATE)
    private Date summerSeasonStart;

    @Temporal(TemporalType.DATE)
    private Date winterSeasonStart;

    @OneToMany(
            mappedBy = "club",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Collection<ClubRelation> users;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.nameSearch = StringUtils.stripAccentsWhitespaceAndToLowerCase(name);
    }

    public String getNameSearch() {
        return nameSearch;
    }

    public void setNameSearch(String nameSearch) {
        this.nameSearch = nameSearch;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Date getSummerSeasonStart() {
        return summerSeasonStart;
    }

    public void setSummerSeasonStart(Date summerSeasonStart) {
        this.summerSeasonStart = summerSeasonStart;
    }

    public Date getWinterSeasonStart() {
        return winterSeasonStart;
    }

    public void setWinterSeasonStart(Date winterSeasonStart) {
        this.winterSeasonStart = winterSeasonStart;
    }

    public Collection<ClubRelation> getUsers() {
        return users;
    }

    public void setUsers(Collection<ClubRelation> users) {
        this.users = users;
    }
}
