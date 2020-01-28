package cz.cvut.fel.tk21.rest.dto.reservation;

public class CreateCyclicReservationDto extends CreateReservationDto {

    private int daysInBetween;

    public CreateCyclicReservationDto() {
        super();
    }

    public int getDaysInBetween() {
        return daysInBetween;
    }

    public void setDaysInBetween(int daysInBetween) {
        this.daysInBetween = daysInBetween;
    }
}
