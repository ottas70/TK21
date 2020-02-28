package cz.cvut.fel.tk21.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Embeddable
public class FromToDate {

    @Column(name = "FROM_DATE", columnDefinition = "DATE")
    private LocalDate from;

    @Column(name = "TO_DATE", columnDefinition = "DATE")
    private LocalDate to;

    public FromToDate() {
    }

    public FromToDate(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    public FromToDate(String from, String to){
        this.from = LocalDate.parse(from, DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        this.to = LocalDate.parse(to, DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    public LocalDate getFrom() {
        return from;
    }

    @JsonSetter(nulls = Nulls.SET)
    public void setFrom(LocalDate from) {
        this.from = from;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    public LocalDate getTo() {
        return to;
    }

    @JsonSetter(nulls = Nulls.SET)
    public void setTo(LocalDate to) {
        this.to = to;
    }

    public boolean containsDate(LocalDate date){
        return (date.isAfter(from) && date.isBefore(to)) || date.isEqual(from) || date.isEqual(to);
    }

}
