package cz.cvut.fel.tk21.ws.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import java.time.LocalDate;

public class DateDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    private LocalDate date;

    public DateDto() {
    }

    public DateDto(LocalDate date) {
        this.date = date;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    public LocalDate getDate() {
        return date;
    }

    @JsonSetter(nulls = Nulls.SET)
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
