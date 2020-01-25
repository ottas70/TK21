package cz.cvut.fel.tk21.rest.dto.club.settings;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.ReservationPermission;

public class PropertiesDto {

    private ReservationPermission reservationPermission;

    private int minReservation;

    private int maxReservation;

    public PropertiesDto(Club club) {
        this.reservationPermission = club.getReservationPermission();
        this.minReservation = club.getMinReservationTime();
        this.maxReservation = club.getMaxReservationTime();
    }

    public ReservationPermission getReservationPermission() {
        return reservationPermission;
    }

    public void setReservationPermission(ReservationPermission reservationPermission) {
        this.reservationPermission = reservationPermission;
    }

    public int getMinReservation() {
        return minReservation;
    }

    public void setMinReservation(int minReservation) {
        this.minReservation = minReservation;
    }

    public int getMaxReservation() {
        return maxReservation;
    }

    public void setMaxReservation(int maxReservation) {
        this.maxReservation = maxReservation;
    }
}
