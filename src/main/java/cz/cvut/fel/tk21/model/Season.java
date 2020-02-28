package cz.cvut.fel.tk21.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
public class Season {

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "from", column = @Column(name = "SUMMER_FROM_DATE")),
            @AttributeOverride(name = "to", column = @Column(name = "SUMMER_TO_DATE"))
    })
    private FromToDate summer;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "from", column = @Column(name = "WINTER_FROM_DATE")),
            @AttributeOverride(name = "to", column = @Column(name = "WINTER_TO_DATE"))
    })
    private FromToDate winter;

    public Season() {
    }

    public Season(FromToDate summer, FromToDate winter) {
        this.summer = summer;
        this.winter = winter;
    }

    public FromToDate getSummer() {
        return summer;
    }

    public void setSummer(FromToDate summer) {
        this.summer = summer;
    }

    public FromToDate getWinter() {
        return winter;
    }

    public void setWinter(FromToDate winter) {
        this.winter = winter;
    }

    /**
     *
     * @return String "winter" or "summer", @null if date is not from season
     *
     */
    public String getSeasonName(LocalDate date){
        if(summer.containsDate(date)) return "summer";
        if(winter.containsDate(date)) return "winter";
        return null;
    }

    public FromToDate getSpecificSeason(LocalDate date){
        String name = getSeasonName(date);
        if(name == null) return null;
        if(name.equals("summer")) return summer;
        if(name.equals("winter")) return winter;
        return null;
    }
}
