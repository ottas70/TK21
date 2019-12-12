package cz.cvut.fel.tk21.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@Entity
public class OpeningHours extends AbstractEntity {

    @OneToOne(mappedBy = "openingHours")
    private Club club;

    @ElementCollection
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Day, FromToTime> openingHours;

    @ElementCollection
    private Map<LocalDate, FromToTime> specialDays;

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Map<Day, FromToTime> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(Map<Day, FromToTime> openingHours) {
        this.openingHours = openingHours;
    }

    public Map<LocalDate, FromToTime> getSpecialDays() {
        return specialDays;
    }

    public void setSpecialDays(Map<LocalDate, FromToTime> specialDays) {
        this.specialDays = specialDays;
    }

    public void addSpecialDate(LocalDate date, FromToTime time){
        specialDays.put(date, time);
    }

    public void removeSpecialDate(LocalDate date){
        specialDays.remove(date);
    }

    public boolean containsSpecialDate(LocalDate date){
        return specialDays.containsKey(date);
    }

    public void updateSpecialDate(LocalDate date, FromToTime fromToTime){
        specialDays.put(date, fromToTime);
    }
}
