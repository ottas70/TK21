package cz.cvut.fel.tk21.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class OpeningHours extends AbstractEntity {

    @OneToOne(mappedBy = "openingHours")
    private Club club;

    @ElementCollection
    @Column(length = 10000)
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Day, FromToTime> openingHours;

    @ElementCollection
    @Column(length = 10000)
    private Map<LocalDate, FromToTime> specialDays;

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Map<Day, FromToTime> getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(Map<Day, FromToTime> openingHours) {
        this.openingHours = openingHours;
    }

    public Map<LocalDate, FromToTime> getSpecialDays() {
        return specialDays;
    }

    public void setSpecialDays(Map<LocalDate, FromToTime> specialDays) {
        this.specialDays = specialDays;
    }

    public void addSpecialDate(LocalDate date, FromToTime time){
        specialDays.put(date, time);
    }

    public void removeSpecialDate(LocalDate date){
        specialDays.remove(date);
    }

    public boolean containsSpecialDate(LocalDate date){
        return specialDays.containsKey(date);
    }

    public void updateSpecialDate(LocalDate date, FromToTime fromToTime){
        specialDays.put(date, fromToTime);
    }

    public Map<LocalDate, FromToTime> getSpecialDaysInYear(int year){
        return specialDays.entrySet().stream()
                .filter(x -> x.getKey().getYear() == year)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean isOpenedAtDate(LocalDate date){
        if(containsSpecialDate(date)){
            return specialDays.get(date).getFrom() != null;
        }

        Day day = Day.getDayFromCode(date.getDayOfWeek().getValue());
        FromToTime hours = openingHours.get(day);

        return hours.getFrom() != null;
    }

    public boolean isOpenedAtDateAndTime(LocalDate date, FromToTime time){
        FromToTime hours = null;
        if(!isOpenedAtDate(date)) return false;
        if(containsSpecialDate(date)){
            hours = specialDays.get(date);
        }else{
            Day day = Day.getDayFromCode(date.getDayOfWeek().getValue());
            hours = openingHours.get(day);
        }

        //Start is before opening
        if(time.getFrom().isBefore(hours.getFrom())) return false;

        //Start after closing
        if(time.getFrom().isAfter(hours.getTo())) return false;

        //End is before opening
        if(time.getTo().isBefore(hours.getFrom())) return false;

        //End after closing
        if(time.getTo().isAfter(hours.getTo())) return false;

        return true;
    }

    public boolean isAfterOpeningAtThisTimeAndDate(LocalDate date, LocalTime time){
        FromToTime hours = null;
        if(!isOpenedAtDate(date)) return true;
        if(containsSpecialDate(date)){
            hours = specialDays.get(date);
        }else{
            Day day = Day.getDayFromCode(date.getDayOfWeek().getValue());
            hours = openingHours.get(day);
        }
        return time.isAfter(hours.getTo());
    }

    public FromToTime getOpeningTimesAtDate(LocalDate date){
        if(!isOpenedAtDate(date)) return null;
        return openingHours.get(Day.getDayFromCode(date.getDayOfWeek().getValue()));
    }
}
