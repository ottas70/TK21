package cz.cvut.fel.tk21.model;

import javax.persistence.*;
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
    @MapKeyTemporal(TemporalType.DATE)
    private Map<Date, FromToTime> specialDays;

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

    public Map<Date, FromToTime> getSpecialDays() {
        return specialDays;
    }

    public void setSpecialDays(Map<Date, FromToTime> specialDays) {
        this.specialDays = specialDays;
    }
}
