package cz.cvut.fel.tk21.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    public void setFrom(LocalTime from) {
        this.from = from;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    public LocalTime getTo() {
        return to;
    }

    @JsonSetter(nulls = Nulls.SET)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    public void setTo(LocalTime to) {
        this.to = to;
    }

    @JsonIgnore
    public boolean isValidOpeningHour(){
        if(from == null && to == null) return true; //Closed
        if(from == null || to == null) return false; //Only one time is set
        if(from.isAfter(to) || from.equals(to)) return false; //Does not make sense
        if(from.getMinute() != 0 && from.getMinute() != 30) return false; //Opening time is incorrect
        if(to.getHour() == 23 && to.getMinute() == 59) return true; //Last available time
        if(to.getMinute() != 0 && to.getMinute() != 30) return false; //Closing time is incorrect
        return true;
    }

    @JsonIgnore
    public boolean isValidReservationTime(){
        if(from == null && to == null) return true; //No time set
        if(from == null || to == null) return false; //Only one time is set)
        if(from.isAfter(to) || from.equals(to)) return false; //Does not make sense
        if(from.getMinute() != 0 && from.getMinute() != 30) return false; //From time is incorrect
        if(to.getHour() == 23 && to.getMinute() == 59) return true; //Last available time
        if(to.getMinute() != 0 && to.getMinute() != 30) return false; //To time is incorrect
        return true;
    }
}
