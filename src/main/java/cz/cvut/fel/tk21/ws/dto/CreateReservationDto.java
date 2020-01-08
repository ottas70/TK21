package cz.cvut.fel.tk21.ws.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.tk21.model.FromToTime;
import cz.cvut.fel.tk21.model.Reservation;

import java.time.LocalDate;

public class CreateReservationDto {

    private String email;

    private LocalDate date;

    private FromToTime time;

    private int courtId;

    private int clubId;

    public CreateReservationDto() {
    }

    public CreateReservationDto(Reservation reservation) {
        this.email = reservation.getEmail();
        this.date = reservation.getDate();
        this.time = reservation.getFromToTime();
        this.courtId = reservation.getTennisCourt().getId();
        this.clubId = reservation.getClub().getId();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    @JsonIgnore
    public Reservation getEntity(){
        Reservation reservation = new Reservation();

        reservation.setDate(date);
        reservation.setEmail(email);
        reservation.setFromToTime(time);

        return reservation;
    }
}
