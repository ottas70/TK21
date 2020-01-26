package cz.cvut.fel.tk21.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class CreatedAtDto {

    private LocalDate date;

    private LocalTime time;

    public CreatedAtDto() {
    }

    public CreatedAtDto(Date date){
        this.date = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        this.time = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
    }

    public CreatedAtDto(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
