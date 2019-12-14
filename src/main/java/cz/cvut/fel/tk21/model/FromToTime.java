package cz.cvut.fel.tk21.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

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

    public FromToTime(String from, String to){
        this.from = LocalTime.parse(from);
        this.to = LocalTime.parse(to);
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    public LocalTime getFrom() {
        return from;
    }

    @JsonSetter(nulls = Nulls.SET)
    public void setFrom(LocalTime from) {
        this.from = from;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    public LocalTime getTo() {
        return to;
    }

    @JsonSetter(nulls = Nulls.SET)
    public void setTo(LocalTime to) {
        this.to = to;
    }
}