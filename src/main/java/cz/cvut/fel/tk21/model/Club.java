package cz.cvut.fel.tk21.model;

import cz.cvut.fel.tk21.util.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.*;

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
    private Address address;

    @Column
    private boolean onlyRegisteredPlayerReservation = true;

    @ElementCollection
    private Map<Integer, Season> seasons;

    @OneToMany(
            mappedBy = "club",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Collection<ClubRelation> users;

    @OneToMany(
            mappedBy = "club",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TennisCourt> courts = new ArrayList<>();


    @OneToOne(cascade = CascadeType.ALL)
    private OpeningHours openingHours;

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

    public boolean isOnlyRegisteredPlayerReservation() {
        return onlyRegisteredPlayerReservation;
    }

    public void setOnlyRegisteredPlayerReservation(boolean onlyRegisteredPlayerReservation) {
        this.onlyRegisteredPlayerReservation = onlyRegisteredPlayerReservation;
    }

    public Map<Integer, Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(Map<Integer, Season> seasons) {
        this.seasons = seasons;
    }

    public Collection<ClubRelation> getUsers() {
        return users;
    }

    public void setUsers(Collection<ClubRelation> users) {
        this.users = users;
    }

    public List<TennisCourt> getCourts() {
        return courts;
    }

    public void setCourts(List<TennisCourt> courts) {
        this.courts = courts;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public void addCourt(TennisCourt court) {
        courts.add(court);
        court.setClub(this);
    }

    public void removeCourt(TennisCourt court) {
        courts.remove(court);
        court.setClub(null);
    }

    public Season getSeasonInYear(int year){
        return seasons.get(year);
    }

    public void addSeasonInYear(int year, Season season){
        seasons.put(year, season);
    }

}
