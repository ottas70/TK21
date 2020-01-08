package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.FromToTime;
import cz.cvut.fel.tk21.model.Reservation;
import cz.cvut.fel.tk21.model.TennisCourt;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservationDao extends BaseDao<Reservation> {

    protected ReservationDao() {
        super(Reservation.class);
    }

    public List<Reservation> findAllReservationsByClubAndDate(Club club, LocalDate date){
        return em.createQuery("SELECT r FROM Reservation r " +
                "WHERE r.date = :date AND r.club = :club", Reservation.class)
                .setParameter("date", date)
                .setParameter("club", club)
                .getResultList();
    }

    public List<Reservation> findAllReservationsByCourtAndDate(TennisCourt tennisCourt, LocalDate date){
        return em.createQuery("SELECT r FROM Reservation r " +
                "WHERE r.date = :date AND r.tennisCourt = :court", Reservation.class)
                .setParameter("date", date)
                .setParameter("court", tennisCourt)
                .getResultList();
    }

    public Optional<Reservation> findAllReservationsByCourtIdDateAndTime(Integer courtId, LocalDate date, FromToTime time){
        try{
            return Optional.ofNullable(em.createQuery("SELECT r FROM Reservation r " +
                    "WHERE r.tennisCourt.id = :id AND r.date = :date AND r.fromToTime = :time", Reservation.class)
                    .setParameter("id", courtId)
                    .setParameter("date", date)
                    .setParameter("time", time)
                    .getSingleResult());
        } catch (NoResultException e){
            return Optional.empty();
        }
    }

}
