package cz.cvut.fel.tk21.ws.dto.helperDto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.tk21.model.FromToDate;
import cz.cvut.fel.tk21.model.Season;

public class CurrentSeasonDto {

    private String name;

    private FromToDate dates;

    private boolean reservationsEnabled;

    public CurrentSeasonDto() {
    }

    public CurrentSeasonDto(String name, FromToDate dates, boolean reservationsEnabled) {
        this.name = name;
        this.dates = dates;
        this.reservationsEnabled = reservationsEnabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FromToDate getDates() {
        return dates;
    }

    public void setDates(FromToDate dates) {
        this.dates = dates;
    }

    public boolean isReservationsEnabled() {
        return reservationsEnabled;
    }

    public void setReservationsEnabled(boolean reservationsEnabled) {
        this.reservationsEnabled = reservationsEnabled;
    }
}
