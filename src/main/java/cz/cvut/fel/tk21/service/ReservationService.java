package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.ReservationDao;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.*;
import cz.cvut.fel.tk21.rest.dto.club.CourtDto;
import cz.cvut.fel.tk21.rest.dto.reservation.ReservationDto;
import cz.cvut.fel.tk21.ws.dto.CurrentSeasonDto;
import cz.cvut.fel.tk21.ws.dto.ReservationMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService extends BaseService<ReservationDao, Reservation> {

    protected ReservationService(ReservationDao dao) {
        super(dao);
    }

    @Transactional(readOnly = true)
    public ReservationMessage initialReservationMessage(Club club, LocalDate date){
        ReservationMessage message = new ReservationMessage();

        Season season = club.getSeasonByDate(date);
        FromToDate seasonDates = null;
        String seasonName = null;
        if (season != null) {
            seasonDates = season.getSpecificSeason(date);
            seasonName = season.getSeasonName(date);
        }
        List<Reservation> reservations = dao.findAllReservationsByClubAndDate(club, date);

        message.setDate(date);
        message.setSeason(new CurrentSeasonDto(seasonName, seasonDates));
        message.setOpeningHours(club.getOpeningHoursByDate(date));
        message.setCourts(club.getAllAvailableCourts(date).stream().map(CourtDto::new).collect(Collectors.toList()));
        message.setReservations(reservations.stream().map(ReservationDto::new).collect(Collectors.toList()));

        return message;
    }

    @Transactional(readOnly = true)
    public LocalDate findNearestAvailableReservationDate(Club club){
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        OpeningHours openingHours = club.getOpeningHours();
        if(openingHours.isOpenedAtDate(date) && !openingHours.isAfterOpeningAtThisTimeAndDate(date, time)) return date;
        date.plusDays(1);
        for (int i = 0; i < 365; i++) {
            if(openingHours.isOpenedAtDate(date)) return date;
            date.plusDays(1);
        }
        throw new ValidationException("Club is closed within next year");
    }

}
