package cz.cvut.fel.tk21.ws.dto;

import cz.cvut.fel.tk21.model.Reservation;
import cz.cvut.fel.tk21.ws.dto.helperDto.UpdateType;

public class UpdateReservationMessage {

    private UpdateType type;

    private ReservationInfo reservation;

    public UpdateReservationMessage(UpdateType type, Reservation reservation, boolean editable, boolean mine) {
        this.type = type;
        this.reservation = new ReservationInfo(reservation, editable, mine);
    }

    public UpdateType getType() {
        return type;
    }

    public void setType(UpdateType type) {
        this.type = type;
    }

    public ReservationInfo getReservation() {
        return reservation;
    }

    public void setReservation(ReservationInfo reservation) {
        this.reservation = reservation;
    }
}
