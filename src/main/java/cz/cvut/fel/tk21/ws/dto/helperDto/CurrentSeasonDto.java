package cz.cvut.fel.tk21.ws.dto.helperDto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.tk21.model.FromToDate;
import cz.cvut.fel.tk21.model.Season;

public class CurrentSeasonDto {

    private String name;

    private FromToDate dates;

    public CurrentSeasonDto() {
    }

    public CurrentSeasonDto(String name, FromToDate dates) {
        this.name = name;
        this.dates = dates;
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
}
