package cz.cvut.fel.tk21.ws.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.tk21.model.FromToTime;
import cz.cvut.fel.tk21.model.Reservation;

public class CreateReservationDto {

    private String email;

    private FromToTime time;

    private int courtId;

    public CreateReservationDto() {
    }

    public CreateReservationDto(Reservation reservation) {
        this.email = reservation.getEmail();
        this.time = reservation.getFromToTime();
        this.courtId = reservation.getTennisCourt().getId();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public FromToTime getTime() {
        return time;
    }

    public void setTime(FromToTime time) {
        this.time = time;
    }

    public int getCourtId() {
        return courtId;
    }

    public void setCourtId(int courtId) {
        this.courtId = courtId;
    }

    @JsonIgnore
    public Reservation getEntity(){
        Reservation reservation = new Reservation();

        reservation.setEmail(email);
        reservation.setFromToTime(time);

        return reservation;
    }
}
