package cz.cvut.fel.tk21.rest.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.Collection;

public class CyclicReservationReport {

    private int id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    private Collection<LocalDate> successful;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    private Collection<LocalDate> failed;

    public CyclicReservationReport(Collection<LocalDate> successful, Collection<LocalDate> failed) {
        this.successful = successful;
        this.failed = failed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Collection<LocalDate> getSuccessful() {
        return successful;
    }

    public void setSuccessful(Collection<LocalDate> successful) {
        this.successful = successful;
    }

    public Collection<LocalDate> getFailed() {
        return failed;
    }

    public void setFailed(Collection<LocalDate> failed) {
        this.failed = failed;
    }
}
