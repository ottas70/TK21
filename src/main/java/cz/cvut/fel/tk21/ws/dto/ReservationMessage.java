package cz.cvut.fel.tk21.ws.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import cz.cvut.fel.tk21.model.FromToTime;
import cz.cvut.fel.tk21.rest.dto.club.CourtDto;
import cz.cvut.fel.tk21.rest.dto.club.SeasonDto;
import cz.cvut.fel.tk21.rest.dto.reservation.ReservationDto;

import java.time.LocalDate;
import java.util.List;

public class ReservationMessage {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    private LocalDate date;

    private String clubName;

    private CurrentSeasonDto season;

    private FromToTime openingHours;

    private List<CourtDto> courts;

    private List<ReservationDto> reservations;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public CurrentSeasonDto getSeason() {
        return season;
    }

    public void setSeason(CurrentSeasonDto season) {
        this.season = season;
    }

    public FromToTime getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(FromToTime openingHours) {
        this.openingHours = openingHours;
    }

    public List<CourtDto> getCourts() {
        return courts;
    }

    public void setCourts(List<CourtDto> courts) {
        this.courts = courts;
    }

    public List<ReservationDto> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationDto> reservations) {
        this.reservations = reservations;
    }
}
