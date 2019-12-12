package cz.cvut.fel.tk21.rest.dto.club;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.FromToTime;
import cz.cvut.fel.tk21.model.OpeningHours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClubSettingsDto {

    private Map<Integer, FromToTime> openingHours;

    private List<SpecialOpeningHoursDto> specialDays;

    private List<CourtDto> courts;

    public ClubSettingsDto(Club club){
        this.openingHours = new HashMap<>();
        club.getOpeningHours().getOpeningHours().forEach((k,v) -> this.openingHours.put(k.getCode(), v));

        this.specialDays = new ArrayList<>();
        club.getOpeningHours().getSpecialDays()
                .forEach((k,v) -> this.specialDays.add(new SpecialOpeningHoursDto(k, v.getFrom(), v.getTo())));

        this.courts = new ArrayList<>();
        club.getCourts().forEach((c) -> this.courts.add(new CourtDto(c)));

    }

    public ClubSettingsDto(OpeningHours openingHours, List<SpecialOpeningHoursDto> specialDays, List<CourtDto> courts) {
        this.openingHours = new HashMap<>();
        openingHours.getOpeningHours().forEach((k,v) -> this.openingHours.put(k.getCode(), v));
        this.specialDays = specialDays;
        this.courts = courts;
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
}
