package cz.cvut.fel.tk21.model;

import java.io.Serializable;
import java.time.LocalTime;

public class FromToTime implements Serializable {

    private LocalTime from;
    private LocalTime to;

    public FromToTime() {
    }

    public FromToTime(LocalTime from, LocalTime to) {
        this.from = from;
        this.to = to;
    }

    public LocalTime getFrom() {
        return from;
    }

    public void setFrom(LocalTime from) {
        this.from = from;
    }

    public LocalTime getTo() {
        return to;
    }

    public void setTo(LocalTime to) {
        this.to = to;
    }
}
