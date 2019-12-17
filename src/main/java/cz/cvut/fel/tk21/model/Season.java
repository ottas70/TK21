package cz.cvut.fel.tk21.model;

import javax.persistence.Entity;
import java.io.Serializable;

public class Season implements Serializable {

    private FromToDate summer;

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
}
