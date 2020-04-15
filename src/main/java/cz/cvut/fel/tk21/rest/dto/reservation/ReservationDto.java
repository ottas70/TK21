package cz.cvut.fel.tk21.rest.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.cvut.fel.tk21.model.FromToTime;
import cz.cvut.fel.tk21.model.Reservation;

import java.time.LocalDate;

public class ReservationDto {

    private int id;

    private LocalDate date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    private FromToTime time;

    private int courtId;

    private int clubId;

    private String clubName;

    private boolean editable;

    private boolean mine;

    private int cyclicId;

    private String name;

    private String surname;

    private String email;

    public ReservationDto() {
    }

    public ReservationDto(Reservation reservation, boolean editable, boolean mine) {
        this.id = reservation.getId();
        this.date = reservation.getDate();
        this.time = reservation.getFromToTime();
        this.courtId = reservation.getTennisCourt().getId();
        this.clubName = reservation.getClub().getName();
        this.clubId = reservation.getClub().getId();
        this.editable = editable;
        this.mine = mine;
        this.cyclicId = reservation.getCyclicReservationId();
        if(editable){
            if(reservation.getUser() == null){
                this.name = reservation.getName();
                this.surname = reservation.getSurname();
                this.email = reservation.getEmail();
            } else {
                this.name = reservation.getUser().getName();
                this.surname = reservation.getUser().getSurname();
                this.email = reservation.getUser().getEmail();
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
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

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public int getCyclicId() {
        return cyclicId;
    }

    public void setCyclicId(int cyclicId) {
        this.cyclicId = cyclicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
