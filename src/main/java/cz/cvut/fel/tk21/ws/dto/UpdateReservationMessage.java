package cz.cvut.fel.tk21.ws.dto;

import cz.cvut.fel.tk21.model.FromToTime;
import cz.cvut.fel.tk21.model.Reservation;

import java.time.LocalDate;

public class UpdateReservationMessage {

    private UpdateType type;

    private int id;

    private LocalDate date;

    private FromToTime time;

    private int courtId;

    private int clubId;

    public UpdateReservationMessage(UpdateType type, Reservation reservation) {
        this.type = type;
        this.id = reservation.getId();
        this.date = reservation.getDate();
        this.time = reservation.getFromToTime();
        this.courtId = reservation.getTennisCourt().getId();
        this.clubId = reservation.getClub().getId();
    }

    public UpdateType getType() {
        return type;
    }

    public void setType(UpdateType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
