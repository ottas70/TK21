package cz.cvut.fel.tk21.ws.dto.helperDto;

import cz.cvut.fel.tk21.model.FromToTime;

public class AvailableCourtDto {

    private int courtId;

    private FromToTime time;

    public AvailableCourtDto(int courtId, FromToTime time) {
        this.courtId = courtId;
        this.time = time;
    }

    public int getCourtId() {
        return courtId;
    }

    public void setCourtId(int courtId) {
        this.courtId = courtId;
    }

    public FromToTime getTime() {
        return time;
    }

    public void setTime(FromToTime time) {
        this.time = time;
    }
}
