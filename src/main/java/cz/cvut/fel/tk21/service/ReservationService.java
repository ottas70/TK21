package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.ReservationDao;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.*;
import cz.cvut.fel.tk21.rest.dto.club.CourtDto;
import cz.cvut.fel.tk21.rest.dto.reservation.ReservationDto;
import cz.cvut.fel.tk21.rest.dto.reservation.CreateReservationDto;
import cz.cvut.fel.tk21.rest.dto.reservation.UpdateReservationDto;
import cz.cvut.fel.tk21.ws.dto.AvailableCourtDto;
import cz.cvut.fel.tk21.ws.dto.CurrentSeasonDto;
import cz.cvut.fel.tk21.ws.dto.ReservationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService extends BaseService<ReservationDao, Reservation> {

    @Autowired
    private ClubService clubService;

    @Autowired
    private CourtService courtService;

    @Autowired
    private UserService userService;

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
            if(seasonDates == null){
                message.setSeason(null);
            } else {
                message.setSeason(new CurrentSeasonDto(seasonName, seasonDates));
            }
        }

        List<Reservation> reservations = this.findAllReservationsByClubAndDate(club, date);

        message.setDate(date);
        message.setClubName(club.getName());
        message.setOpeningHours(club.getOpeningHoursByDate(date));
        message.setFirstAvailableTime(findNearestAvailableTime(club, date));
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
        date = date.plusDays(1);
        for (int i = 0; i < 365; i++) {
            if(openingHours.isOpenedAtDate(date)) return date;
            date = date.plusDays(1);
        }
        throw new ValidationException("Club is closed within next year");
    }

    @Transactional(readOnly = true)
    public AvailableCourtDto findNearestAvailableTime(Club club, LocalDate date){
        if(!club.getOpeningHours().isOpenedAtDate(date)) return null;
        FromToTime openingHours = club.getOpeningHours().getOpeningTimesAtDate(date);
        LocalTime from = openingHours.getFrom();
        while(from.plusHours(1).isBefore(openingHours.getTo()) || from.plusHours(1).equals(openingHours.getTo())){
            for (TennisCourt court : club.getCourts()){
                FromToTime reservationTime = new FromToTime(from, from.plusHours(1));
                if(courtService.isCourtAvailable(club, court, date, reservationTime)){
                    return new AvailableCourtDto(court.getId(), reservationTime);
                }
            }
            from = from.plusMinutes(15);
        }
        return null;
    }

    @Transactional
    public void createReservationFromDTO(CreateReservationDto dto, Club club, LocalDate date){
        if(!dto.getTime().isValidReservationTime()) throw new ValidationException("Neplatný čas rezervace");
        if(date.isBefore(LocalDate.now())) throw new ValidationException("Na tento termín nelze kurt rezervovat");
        if(date.equals(LocalDate.now()) && dto.getTime().getFrom().isBefore(LocalTime.now())) throw new ValidationException("Na tento termín nelze kurt rezervovat");

        Reservation reservation = dto.getEntity();
        User currentUser = userService.getCurrentUser();
        if(currentUser != null){
            reservation.setEmail(currentUser.getEmail());
            reservation.setName(currentUser.getName());
            reservation.setSurname(currentUser.getSurname());
            reservation.setUser(currentUser);
        }

        if(reservation.getEmail() == null || reservation.getName() == null || reservation.getSurname() == null) throw new ValidationException("Špatně vyplněné údaje");

        Optional<TennisCourt> courtOptional = courtService.findCourtInClub(club, dto.getCourtId());
        courtOptional.orElseThrow(() -> new NotFoundException("Tenisový kurt nebyl nalezen"));
        TennisCourt court = courtOptional.get();

        reservation.setClub(club);
        reservation.setTennisCourt(court);

        if(!courtService.isCourtAvailable(club, court, date, dto.getTime())) throw new ValidationException("Kurt není v tento čas k dispozici");

        dao.persist(reservation);
    }

    @Transactional
    public void updateReservation(Reservation reservation, UpdateReservationDto updateDto){
        if(!updateDto.getTime().isValidReservationTime()) throw new ValidationException("Neplatný čas rezervace");
        if(updateDto.getDate().isBefore(LocalDate.now())) throw new ValidationException("Na tento termín nelze kurt rezervovat");
        if(updateDto.getDate().equals(LocalDate.now()) && updateDto.getTime().getFrom().isBefore(LocalTime.now())) throw new ValidationException("Na tento termín nelze kurt rezervovat");

        Optional<TennisCourt> courtOptional = courtService.findCourtInClub(reservation.getClub(), updateDto.getCourtId());
        courtOptional.orElseThrow(() -> new NotFoundException("Tenisový kurt nebyl nalezen"));
        TennisCourt court = courtOptional.get();

        if(!courtService.isCourtAvailableForUpdate(reservation.getClub(), court, updateDto.getDate(), updateDto.getTime(), reservation)) throw new ValidationException("Kurt není v tento čas k dispozici");

        reservation.setTennisCourt(court);
        reservation.setDate(updateDto.getDate());
        reservation.setFromToTime(updateDto.getTime());

        dao.update(reservation);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findAllReservationsByClubAndDate(Club club, LocalDate date){
        return dao.findAllReservationsByClubAndDate(club, date);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findAllReservationsByCourtAndDate(TennisCourt tennisCourt, LocalDate date){
        return dao.findAllReservationsByCourtAndDate(tennisCourt, date);
    }

    @Transactional(readOnly = true)
    public Optional<Reservation> findReservationByCourtIdDateAndTime(Integer courtId, LocalDate date, FromToTime time){
        return dao.findAllReservationsByCourtIdDateAndTime(courtId, date, time);
    }

    @Transactional
    public void deleteReservation(Reservation reservation){
        this.remove(reservation);
    }

}
