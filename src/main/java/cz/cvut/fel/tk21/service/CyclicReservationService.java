package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.CyclicReservationDao;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.*;
import cz.cvut.fel.tk21.rest.dto.reservation.CreateCyclicReservationDto;
import cz.cvut.fel.tk21.rest.dto.reservation.CyclicReservationReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CyclicReservationService extends BaseService<CyclicReservationDao, CyclicReservation> {

    @Autowired
    private ClubService clubService;

    @Autowired
    private ReservationService reservationService;

    protected CyclicReservationService(CyclicReservationDao dao) {
        super(dao);
    }

    @Transactional
    public CyclicReservation createCyclicReservation(int daysInBetween){
        CyclicReservation cyclicReservation = new CyclicReservation();
        cyclicReservation.setDaysInBetween(daysInBetween);

        return this.persist(cyclicReservation);
    }

    @Transactional
    public CyclicReservationReport createReservationsBasedOnCyclicReservation(CyclicReservation cyclicReservation, CreateCyclicReservationDto dto, Club club, LocalDate date, int numOfReservations){
        if(!clubService.isCurrentUserAllowedToManageThisClub(club)) throw new UnauthorizedException("Přístup odepřen");

        LocalDate initialDate = date;
        Season currentSeason = club.getSeasonByDate(date);
        if(currentSeason == null) throw new ValidationException("V tomto termínu nelze rezervovat kurt");
        FromToDate seasonTime = currentSeason.getSpecificSeason(date);
        if(seasonTime == null) throw new ValidationException("V tomto termínu nelze rezervovat kurt");

        List<LocalDate> successful = new ArrayList<>();
        List<LocalDate> failed = new ArrayList<>();

        int counter = 0;
        while(!date.isAfter(seasonTime.getTo()) && counter < numOfReservations){
            try{
                Reservation reservation = reservationService.createReservationFromDTO(dto, club, date);
                reservation.setCyclicReservationId(cyclicReservation.getId());
                if(date.equals(initialDate)) cyclicReservation.setInitialReservation(reservation);
                successful.add(date);
                reservationService.update(reservation);
            } catch (Exception ex){
                if(date.equals(initialDate)) throw new ValidationException("V tento den nelze kurt rezervovat");
                failed.add(date);
            }
            date = date.plusDays(cyclicReservation.getDaysInBetween());
            counter++;
        }

        this.update(cyclicReservation);
        return new CyclicReservationReport(successful, failed);
    }

    @Transactional
    public List<Reservation> findAllReservationsByCyclicID(Integer cyclid_id){
        return reservationService.findReservationByCyclicId(cyclid_id);
    }

    @Transactional
    public List<Reservation> deleteCyclicReservation(CyclicReservation cyclicReservation){
        if(!clubService.isCurrentUserAllowedToManageThisClub(cyclicReservation.getInitialReservation().getClub())) throw new UnauthorizedException("Přístup odepřen");

        this.remove(cyclicReservation);

        List<Reservation> reservations = this.findAllReservationsByCyclicID(cyclicReservation.getId());
        for (Reservation r : reservations){
            reservationService.remove(r);
        }

        return reservations;
    }

}
