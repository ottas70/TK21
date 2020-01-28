package cz.cvut.fel.tk21.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class CyclicReservation extends AbstractEntity {

    @OneToOne
    private Reservation initialReservation;

    @Column
    private int daysInBetween;

    public Reservation getInitialReservation() {
        return initialReservation;
    }

    public void setInitialReservation(Reservation initialReservation) {
        this.initialReservation = initialReservation;
    }

    public int getDaysInBetween() {
        return daysInBetween;
    }

    public void setDaysInBetween(int daysInBetween) {
        this.daysInBetween = daysInBetween;
    }
}
