package cz.cvut.fel.tk21.rest.dto.club;

public class ReservationSeasonSettingsDto {

    private int year;

    private boolean winter;

    private boolean enable;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isWinter() {
        return winter;
    }

    public void setWinter(boolean winter) {
        this.winter = winter;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
