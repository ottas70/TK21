package cz.cvut.fel.tk21.rest.dto.reservation;

import cz.cvut.fel.tk21.model.FromToTime;
import cz.cvut.fel.tk21.model.Reservation;

import java.time.LocalDate;

public class ReservationDto {

    private LocalDate date;

    private FromToTime time;

    private int courtId;

    private int clubId;

    public ReservationDto() {
    }

    public ReservationDto(Reservation reservation) {
        this.date = reservation.getDate();
        this.time = reservation.getFromToTime();
        this.courtId = reservation.getTennisCourt().getId();
        this.clubId = reservation.getClub().getId();
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
}
