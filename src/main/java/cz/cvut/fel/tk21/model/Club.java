package cz.cvut.fel.tk21.model;

import cz.cvut.fel.tk21.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "Club",
        indexes = {@Index(name = "nameSearch_index", columnList = "nameSearch", unique = false)})
public class Club extends AbstractEntity {

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Column(name = "nameSearch", nullable = false)
    private String nameSearch;

    @Column
    private String email;

    @Column
    private String telephone;

    @Column
    private String web;

    @OneToOne(cascade = CascadeType.ALL)
    private Address address;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private ReservationPermission reservationPermission;

    @Column
    private int minReservationTime;

    @Column
    private int maxReservationTime;

    @ElementCollection
    @CollectionTable(name = "SEASON")
    private Map<Integer, Season> seasons;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<ClubRelation> users;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TennisCourt> courts = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private OpeningHours openingHours;

    @OneToMany(mappedBy = "club", cascade = CascadeType.PERSIST)
    private Collection<VerificationRequest> verificationRequests;

    @OneToMany(mappedBy = "club", cascade = CascadeType.PERSIST)
    private Collection<Post> posts;

    @OneToMany
    private Set<User> blocked;

    /*** For Web scraping ***/

    @Column
    private int webId;

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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReservationPermission getReservationPermission() {
        return reservationPermission;
    }

    public void setReservationPermission(ReservationPermission reservationPermission) {
        this.reservationPermission = reservationPermission;
    }

    public int getMinReservationTime() {
        return minReservationTime;
    }

    public void setMinReservationTime(int minReservationTime) {
        this.minReservationTime = minReservationTime;
    }

    public int getMaxReservationTime() {
        return maxReservationTime;
    }

    public void setMaxReservationTime(int maxReservationTime) {
        this.maxReservationTime = maxReservationTime;
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

    public Set<User> getBlocked() {
        return blocked;
    }

    public void setBlocked(Set<User> blocked) {
        this.blocked = blocked;
    }

    public int getWebId() {
        return webId;
    }

    public void setWebId(int webId) {
        this.webId = webId;
    }

    public void addCourt(TennisCourt court) {
        courts.add(court);
        court.setClub(this);
    }

    public void removeCourt(TennisCourt court) {
        courts.remove(court);
        court.setClub(null);
    }
    
    public Season getSeasonByDate(LocalDate date){
        for (Map.Entry<Integer, Season> entry : seasons.entrySet()){
            if(date.isAfter(entry.getValue().getSummer().getFrom()) &&
                    date.isBefore(entry.getValue().getWinter().getTo())){
                return entry.getValue();
            }
        }
        return getSeasonInYear(date.getYear());
    }

    public Season getSeasonInYear(int year){
        return seasons.get(year);
    }

    public void addSeasonInYear(int year, Season season){
        seasons.put(year, season);
    }

    public FromToTime getOpeningHoursByDate(LocalDate date){
        if(openingHours.containsSpecialDate(date)){
            return openingHours.getSpecialDays().get(date);
        }

        DayOfWeek day = date.getDayOfWeek();
        Day myDay = Day.getDayFromCode(day.getValue());
        return openingHours.getRegularHours().get(myDay);
    }

    public List<TennisCourt> getAllAvailableCourts(LocalDate date){
        List<TennisCourt> tennisCourts = new ArrayList<>();

        Season season = getSeasonByDate(date);
        if(season == null) return tennisCourts;
        String seasonName = season.getSeasonName(date);
        if(seasonName == null) return tennisCourts;
        for (TennisCourt court : courts){
            if(seasonName.equals("winter") && court.isAvailableInWinter()) tennisCourts.add(court);
            if(seasonName.equals("summer") && court.isAvailableInSummer()) tennisCourts.add(court);
        }
        return tennisCourts;
    }

    public boolean isUserBlocked(User user){
        for (User u : blocked){
            if(u.getId() == user.getId()) return true;
        }
        return false;
    }

    public void addToBlocked(User user){
        blocked.add(user);
    }

    public void removeFromBlocked(User user){
        blocked.removeIf(u -> u.getId() == user.getId());
    }

    public boolean isWebScraped(){
        return webId != 0;
    }

}
