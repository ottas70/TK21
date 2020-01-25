package cz.cvut.fel.tk21.rest.dto.club;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.FromToTime;
import cz.cvut.fel.tk21.rest.dto.club.settings.PropertiesDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClubSettingsDto {

    private Map<Integer, FromToTime> openingHours;

    private List<SpecialOpeningHoursDto> specialDays;

    private List<CourtDto> courts;

    private SeasonDto seasons;

    private PropertiesDto properties;

    public ClubSettingsDto(Club club, int year, boolean isYearSet){
        this.openingHours = new HashMap<>();
        club.getOpeningHours().getOpeningHours().forEach((k,v) -> this.openingHours.put(k.getCode(), v));

        this.specialDays = new ArrayList<>();
        club.getOpeningHours().getSpecialDaysInYear(year)
                .forEach((k,v) -> this.specialDays.add(new SpecialOpeningHoursDto(k, v.getFrom(), v.getTo())));

        this.courts = new ArrayList<>();
        club.getCourts().forEach((c) -> this.courts.add(new CourtDto(c)));

        if(isYearSet){
            this.seasons = new SeasonDto(club.getSeasonInYear(year));
        } else{
            this.seasons = new SeasonDto(club.getSeasonByDate(LocalDate.now()));
        }

        this.properties = new PropertiesDto(club);

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
}
