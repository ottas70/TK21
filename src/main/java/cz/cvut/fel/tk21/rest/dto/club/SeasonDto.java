package cz.cvut.fel.tk21.rest.dto.club;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.tk21.model.FromToDate;
import cz.cvut.fel.tk21.model.Season;

public class SeasonDto {

    private FromToDate winter;

    private boolean winterEnabled;

    private FromToDate summer;

    private boolean summerEnabled;

    public SeasonDto() {
    }

    public SeasonDto(Season season){
        if(season != null){
            this.winter = season.getWinter();
            this.summer = season.getSummer();
            this.winterEnabled = season.isWinterResEnabled();
            this.summerEnabled = season.isWinterResEnabled();
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

    public boolean isWinterEnabled() {
        return winterEnabled;
    }

    public void setWinterEnabled(boolean winterEnabled) {
        this.winterEnabled = winterEnabled;
    }

    public FromToDate getSummer() {
        return summer;
    }

    public void setSummer(FromToDate summer) {
        this.summer = summer;
    }

    public boolean isSummerEnabled() {
        return summerEnabled;
    }

    public void setSummerEnabled(boolean summerEnabled) {
        this.summerEnabled = summerEnabled;
    }

    @JsonIgnore
    public Season getEntity(){
        Season season = new Season();
        season.setSummer(summer);
        season.setSummerResEnabled(summerEnabled);
        season.setWinter(winter);
        season.setWinterResEnabled(winterEnabled);
        return season;
    }
}
