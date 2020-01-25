package cz.cvut.fel.tk21.rest.dto.club.settings;

import cz.cvut.fel.tk21.model.ReservationPermission;

public class ReservationPermissionDto {

    private ReservationPermission reservationPermission;

    public ReservationPermission getReservationPermission() {
        return reservationPermission;
    }

    public void setReservationPermission(ReservationPermission reservationPermission) {
        this.reservationPermission = reservationPermission;
    }
}
