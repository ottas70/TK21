package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.*;
import cz.cvut.fel.tk21.model.tournament.Tournament;
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

    public Optional<Reservation> findReservationByCourtIdDateAndTime(Integer courtId, LocalDate date, FromToTime time){
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

    public Optional<Reservation> findReservationsByToken(String token){
        try{
            return Optional.ofNullable(em.createQuery("SELECT r FROM Reservation r " +
                    "WHERE r.token = :token", Reservation.class)
                    .setParameter("token", token)
                    .getSingleResult());
        } catch (NoResultException e){
            return Optional.empty();
        }
    }

    public List<Reservation> findAllReservationsByCyclicId(int id){
        return em.createQuery("SELECT r FROM Reservation r " +
                "WHERE r.cyclicReservationId = :id", Reservation.class)
                .setParameter("id", id)
                .getResultList();
    }

    public Optional<Reservation> findReservationByCyclicIdAfterDate(int id, LocalDate date){
        try{
            return Optional.ofNullable(em.createQuery("SELECT r FROM Reservation r " +
                    "WHERE r.cyclicReservationId = :id AND r.date > :date " +
                    "ORDER BY r.date ASC", Reservation.class)
                    .setParameter("id", id)
                    .setParameter("date", date)
                    .setMaxResults(1)
                    .getSingleResult());
        } catch (NoResultException e){
            return Optional.empty();
        }
    }

    public List<Reservation> findAllUpcomingReservationsForUser(User user){
        LocalDate now = LocalDate.now();
        return em.createQuery("SELECT r FROM Reservation r " +
                "WHERE r.user = :user AND r.date >= :now", Reservation.class)
                .setParameter("user", user)
                .setParameter("now", now)
                .getResultList();
    }

}
