package cz.cvut.fel.tk21.rest.dto.club;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.tk21.model.FromToDate;
import cz.cvut.fel.tk21.model.Season;

public class SeasonDto {

    private FromToDate winter;

    private FromToDate summer;

    public SeasonDto() {
    }

    public SeasonDto(Season season){
        if(season != null){
            this.winter = season.getWinter();
            this.summer = season.getSummer();
        }
    }

    public SeasonDto(FromToDate winter, FromToDate summer) {
        this.winter = winter;
        this.summer = summer;
    }

    public FromToDate getWinter() {
        return winter;
    }

    public void setWinter(FromToDate winter) {
        this.winter = winter;
    }

    public FromToDate getSummer() {
        return summer;
    }

    public void setSummer(FromToDate summer) {
        this.summer = summer;
    }

    @JsonIgnore
    public Season getEntity(){
        Season season = new Season();
        season.setSummer(summer);
        season.setWinter(winter);
        return season;
    }
}
