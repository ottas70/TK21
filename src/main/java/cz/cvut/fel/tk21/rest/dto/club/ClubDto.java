package cz.cvut.fel.tk21.rest.dto.club;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.FromToTime;
import cz.cvut.fel.tk21.model.tournament.Tournament;
import cz.cvut.fel.tk21.rest.dto.court.CourtDto;
import cz.cvut.fel.tk21.rest.dto.teams.CompetitionDto;
import cz.cvut.fel.tk21.rest.dto.tournament.TournamentDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClubDto {

    private int id;

    private String name;

    private String desc;

    private AddressDto address;

    private List<CourtDto> courts;

    private SeasonDto seasons;

    private Map<Integer, FromToTime> openingHours;

    private List<SpecialOpeningHoursDto> specialDays;

    private ContactDto contact;

    private boolean reservationsEnabled;

    private boolean isAllowedMng;

    private boolean isAllowedRes;

    private boolean isMember;

    private int numOfRequests;

    private boolean isScraped;

    private boolean isRegistered;

    private List<TournamentDto> tournaments;

    private List<CompetitionDto> competitions;

    public ClubDto() {
    }

    public ClubDto(Club club, boolean isAllowedMng, boolean isAllowedRes, boolean isMember, int numOfRequests, List<Tournament> tournaments, List<CompetitionDto> competitions) {
        this.id = club.getId();
        this.name = club.getName();
        this.desc = club.getDescription();
        if(club.getAddress() != null) this.address = new AddressDto(club.getAddress());
        this.courts = club.getCourts().stream().map(CourtDto::new).collect(Collectors.toList());
        this.seasons = new SeasonDto(club.getSeasonByDate(LocalDate.now()));
        if(club.isRegistered()){
            this.openingHours = new HashMap<>();
            club.getOpeningHours().getRegularHours().forEach((k, v) -> this.openingHours.put(k.getCode(), v));
        }
        this.specialDays = new ArrayList<>();
        club.getOpeningHours().getSpecialDaysInNextDays(14)
                .forEach((k,v) -> this.specialDays.add(new SpecialOpeningHoursDto(k, v.getFrom(), v.getTo())));
        this.contact = new ContactDto(club);
        this.reservationsEnabled = club.isReservationsEnabled();
        this.isAllowedMng = isAllowedMng;
        this.isAllowedRes = isAllowedRes;
        this.isMember = isMember;
        this.numOfRequests = numOfRequests;
        this.isScraped = club.isWebScraped();
        this.isRegistered = club.isRegistered();
        this.tournaments = tournaments.stream().sorted(Tournament.getComparator()).map(TournamentDto::new).collect(Collectors.toList());
        this.competitions = competitions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public List<CourtDto> getCourts() {
        return courts;
    }

    public void setCourts(List<CourtDto> courts) {
        this.courts = courts;
    }

    public SeasonDto getSeasons() {
        return seasons;
    }

    public void setSeasons(SeasonDto seasons) {
        this.seasons = seasons;
    }

    public Map<Integer, FromToTime> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(Map<Integer, FromToTime> openingHours) {
        this.openingHours = openingHours;
    }

    public List<SpecialOpeningHoursDto> getSpecialDays() {
        return specialDays;
    }

    public void setSpecialDays(List<SpecialOpeningHoursDto> specialDays) {
        this.specialDays = specialDays;
    }

    public ContactDto getContact() {
        return contact;
    }

    public void setContact(ContactDto contact) {
        this.contact = contact;
    }

    public boolean isReservationsEnabled() {
        return reservationsEnabled;
    }

    public void setReservationsEnabled(boolean reservationsEnabled) {
        this.reservationsEnabled = reservationsEnabled;
    }

    @JsonProperty(value="isAllowedMng")
    public boolean isAllowedMng() {
        return isAllowedMng;
    }

    public void setAllowedMng(boolean allowedMng) {
        isAllowedMng = allowedMng;
    }

    @JsonProperty(value="isAllowedRes")
    public boolean isAllowedRes() {
        return isAllowedRes;
    }

    public void setAllowedRes(boolean allowedRes) {
        isAllowedRes = allowedRes;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public int getNumOfRequests() {
        return numOfRequests;
    }

    public void setNumOfRequests(int numOfRequests) {
        this.numOfRequests = numOfRequests;
    }

    public boolean isScraped() {
        return isScraped;
    }

    public void setScraped(boolean scraped) {
        isScraped = scraped;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public List<TournamentDto> getTournaments() {
        return tournaments;
    }

    public void setTournaments(List<TournamentDto> tournaments) {
        this.tournaments = tournaments;
    }

    public List<CompetitionDto> getCompetitions() {
        return competitions;
    }

    public void setCompetitions(List<CompetitionDto> competitions) {
        this.competitions = competitions;
    }

    @JsonIgnore
    public Club getEntity(){
        Club club = new Club();
        club.setId(this.id);
        club.setName(this.name);
        club.setAddress(this.address.getEntity());
        return club;
    }

}
