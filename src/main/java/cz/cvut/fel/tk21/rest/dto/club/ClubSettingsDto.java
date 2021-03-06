package cz.cvut.fel.tk21.rest.dto.club;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.FromToTime;
import cz.cvut.fel.tk21.rest.dto.club.settings.PropertiesDto;
import cz.cvut.fel.tk21.rest.dto.court.CourtDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClubSettingsDto {

    private AddressDto address;

    private Map<Integer, FromToTime> openingHours;

    private List<SpecialOpeningHoursDto> specialDays;

    private List<CourtDto> courts;

    private ContactDto contact;

    private SeasonDto seasons;

    private PropertiesDto properties;

    private boolean isScraped;

    private boolean reservationsEnabled;

    public ClubSettingsDto(Club club, int year, boolean isYearSet){
        if(club.getAddress() != null) this.address = new AddressDto(club.getAddress());

        if(club.isRegistered()){
            this.openingHours = new HashMap<>();
            club.getOpeningHours().getRegularHours().forEach((k, v) -> {
                if(v == null) v = new FromToTime();
                this.openingHours.put(k.getCode(), v);
            });
        }

        this.specialDays = new ArrayList<>();
        club.getOpeningHours().getSpecialDaysInYear(year)
                .forEach((k,v) -> this.specialDays.add(new SpecialOpeningHoursDto(k, v.getFrom(), v.getTo())));

        this.courts = new ArrayList<>();
        club.getCourts().forEach((c) -> this.courts.add(new CourtDto(c)));

        this.contact = new ContactDto(club);

        if(isYearSet){
            this.seasons = new SeasonDto(club.getSeasonInYear(year));
        } else{
            this.seasons = new SeasonDto(club.getSeasonByDate(LocalDate.now()));
        }

        this.properties = new PropertiesDto(club);
        this.isScraped = club.isWebScraped();
        this.reservationsEnabled = club.isReservationsEnabled();
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
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

    public List<CourtDto> getCourts() {
        return courts;
    }

    public void setCourts(List<CourtDto> courts) {
        this.courts = courts;
    }

    public ContactDto getContact() {
        return contact;
    }

    public void setContact(ContactDto contact) {
        this.contact = contact;
    }

    public SeasonDto getSeasons() {
        return seasons;
    }

    public void setSeasons(SeasonDto seasons) {
        this.seasons = seasons;
    }

    public PropertiesDto getProperties() {
        return properties;
    }

    public void setProperties(PropertiesDto properties) {
        this.properties = properties;
    }

    public boolean isScraped() {
        return isScraped;
    }

    public void setScraped(boolean scraped) {
        isScraped = scraped;
    }

    public boolean isReservationsEnabled() {
        return reservationsEnabled;
    }

    public void setReservationsEnabled(boolean reservationsEnabled) {
        this.reservationsEnabled = reservationsEnabled;
    }
}
