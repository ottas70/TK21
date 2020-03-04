package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.ReservationDao;
import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.*;
import cz.cvut.fel.tk21.rest.dto.court.CourtDto;
import cz.cvut.fel.tk21.rest.dto.reservation.CreateReservationDto;
import cz.cvut.fel.tk21.rest.dto.reservation.ReservationDto;
import cz.cvut.fel.tk21.rest.dto.reservation.UpdateReservationDto;
import cz.cvut.fel.tk21.ws.WebSocketController;
import cz.cvut.fel.tk21.ws.dto.AvailableCourtDto;
import cz.cvut.fel.tk21.ws.dto.CurrentSeasonDto;
import cz.cvut.fel.tk21.ws.dto.ReservationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationService extends BaseService<ReservationDao, Reservation> {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);

    @Autowired
    private ClubService clubService;
    @Autowired
    private CourtService courtService;
    @Autowired
    private UserService userService;
    @Autowired
    private ClubRelationService clubRelationService;
    @Autowired
    private CyclicReservationService cyclicReservationService;

    protected ReservationService(ReservationDao dao) {
        super(dao);
    }

    @Transactional(readOnly = true)
    public ReservationMessage initialReservationMessage(Club club, LocalDate date, User user){
        log.info("Initial Message service method entered");
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
        message.setReservations(reservations.stream().map(r -> new ReservationDto(r, isUserAllowedToEditReservation(user, r), isOwner(r, user))).collect(Collectors.toList()));
        message.setReservationPermission(club.getReservationPermission());
        message.setAuthorized(isUserAllowedToCreateReservation(user, club));

        return message;
    }

    @Transactional(readOnly = true)
    public LocalDate findNearestAvailableReservationDate(Club club){
        log.info("find nearest date method entered");
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
        log.info("find nearest time method entered");
        if(!club.getOpeningHours().isOpenedAtDate(date)) return null;
        FromToTime openingHours = club.getOpeningHours().getOpeningTimesAtDate(date);
        LocalTime now = LocalTime.now();
        LocalTime from = openingHours.getFrom();
        if(now.isAfter(from) && date.equals(LocalDate.now())) from = now.plusHours(1).withMinute(0).withSecond(0);
        List<TennisCourt> availableCourts = club.getAllAvailableCourts(date);
        while(from.plusHours(1).isBefore(openingHours.getTo()) || from.plusHours(1).equals(openingHours.getTo())){
            for (TennisCourt court : availableCourts){
                FromToTime reservationTime = new FromToTime(from, from.plusHours(1));
                if(courtService.isCourtAvailable(club, court, date, reservationTime)){
                    return new AvailableCourtDto(court.getId(), reservationTime);
                }
            }
            from = from.plusMinutes(15);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Reservation createReservationFromDTO(CreateReservationDto dto, Club club, LocalDate date){
        checkReservationPermission(club);
        if(!dto.getTime().isValidReservationTime()) throw new ValidationException("Neplatný čas rezervace.");
        if(date.isBefore(LocalDate.now())) throw new ValidationException("Na tento termín nelze kurt rezervovat.");
        if(date.equals(LocalDate.now()) && dto.getTime().getFrom().isBefore(LocalTime.now())) throw new ValidationException("Na tento termín nelze kurt rezervovat.");

        Reservation reservation = dto.getEntity();
        User currentUser = userService.getCurrentUser();
        if(currentUser != null){
            reservation.setEmail(currentUser.getEmail());
            reservation.setName(currentUser.getName());
            reservation.setSurname(currentUser.getSurname());
            reservation.setUser(currentUser);
        }

        if(reservation.getEmail() == null || reservation.getName() == null || reservation.getSurname() == null) throw new ValidationException("Špatně vyplněné údaje.");
        if(reservation.getDuration() < club.getMinReservationTime() || (reservation.getDuration() > club.getMaxReservationTime()) && club.getMaxReservationTime() != 0) throw new ValidationException("Trvání rezervace nevyhovuje požadavkům klubu.");

        Optional<TennisCourt> courtOptional = courtService.findCourtInClub(club, dto.getCourtId());
        courtOptional.orElseThrow(() -> new NotFoundException("Tenisový kurt nebyl nalezen."));
        TennisCourt court = courtOptional.get();

        reservation.setClub(club);
        reservation.setTennisCourt(court);
        reservation.setDate(date);
        reservation.setToken(UUID.randomUUID().toString());

        if(!courtService.isCourtAvailable(club, court, date, dto.getTime())) throw new ValidationException("Kurt není v tento čas k dispozici.");

        return dao.persist(reservation);
    }

    @Transactional
    public boolean isCurrentUserAllowedToCreateReservation(Club club){
        User user = userService.getCurrentUser();
        return isUserAllowedToCreateReservation(user, club);
    }

    @Transactional
    public boolean isUserAllowedToCreateReservation(User user, Club club){
        if(club.getReservationPermission() == ReservationPermission.ANYONE) return true;
        if(user != null && club.getReservationPermission() == ReservationPermission.SIGNED) return true;
        if(club.getReservationPermission() ==  ReservationPermission.CLUB_MEMBERS){
            return clubRelationService.isMemberOf(club, user);
        }
        return false;
    }

    @Transactional
    public boolean isCurrentUserAllowedToEditReservation(Reservation reservation){
        return isUserAllowedToEditReservation(userService.getCurrentUser(), reservation);
    }

    @Transactional
    public boolean isUserAllowedToEditReservation(User user, Reservation reservation){
        if(user == null) return false;
        return reservation.getUser().getId() == user.getId() || clubService.isUserAllowedToManageThisClub(user, reservation.getClub());
    }

    @Transactional
    public boolean isMine(Reservation reservation){
        return isOwner(reservation, userService.getCurrentUser());
    }

    @Transactional
    public boolean isOwner(Reservation reservation, User user){
        if(user == null) return false;
        return reservation.getUser().getId() == user.getId();
    }

    @Transactional
    public void checkReservationPermission(Club club){
        User user = userService.getCurrentUser();
        if(club.getReservationPermission() == ReservationPermission.SIGNED && user == null) throw new UnauthorizedException("NOT SIGNED IN");
        if(club.getReservationPermission() == ReservationPermission.CLUB_MEMBERS && user == null) throw new UnauthorizedException("NOT SIGNED IN");
        if(club.getReservationPermission() == ReservationPermission.CLUB_MEMBERS
                && !clubRelationService.isMemberOf(club, user)) throw new UnauthorizedException("NOT A MEMBER");
    }

    @Transactional
    public void updateReservation(Reservation reservation, UpdateReservationDto updateDto){
        if(!isUserAllowedToEditReservation(userService.getCurrentUser(), reservation)) throw new UnauthorizedException("Přístup zamítnut.");
        if(!updateDto.getTime().isValidReservationTime()) throw new ValidationException("Neplatný čas rezervace.");
        if(updateDto.getDate().isBefore(LocalDate.now())) throw new ValidationException("Na tento termín nelze kurt rezervovat.");
        if(updateDto.getDate().equals(LocalDate.now()) && updateDto.getTime().getFrom().isBefore(LocalTime.now())) throw new ValidationException("Na tento termín nelze kurt rezervovat.");

        Optional<TennisCourt> courtOptional = courtService.findCourtInClub(reservation.getClub(), updateDto.getCourtId());
        courtOptional.orElseThrow(() -> new NotFoundException("Tenisový kurt nebyl nalezen."));
        TennisCourt court = courtOptional.get();

        if(!courtService.isCourtAvailableForUpdate(reservation.getClub(), court, updateDto.getDate(), updateDto.getTime(), reservation)) throw new ValidationException("Kurt není v tento čas k dispozici.");

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
        return dao.findReservationByCourtIdDateAndTime(courtId, date, time);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findReservationByCyclicId(Integer cyclidId){
        return dao.findAllReservationsByCyclicId(cyclidId);
    }

    @Transactional
    public void deleteReservation(Reservation reservation){
        if(reservation.getUser() == null || reservation.getUser().getId() != userService.getCurrentUser().getId()) throw new UnauthorizedException("Přístup zamítnut");

        if(reservation.getInitialCyclicReservation() != null){
            int cyclicId = reservation.getCyclicReservationId();
            Optional<Reservation> next = dao.findReservationByCyclicIdAfterDate(cyclicId, reservation.getDate());
            Optional<CyclicReservation> cyclicReservation = cyclicReservationService.find(cyclicId);
            if(next.isEmpty()) {
                cyclicReservation.ifPresent(cyclicReservationService::remove);
                reservation.setInitialCyclicReservation(null);
            } else {
                cyclicReservation.get().setInitialReservation(next.get());
                next.get().setInitialCyclicReservation(cyclicReservation.get());
                this.update(next.get());
            }
        }

        this.remove(reservation);
    }

    @Transactional
    public void deleteReservationByToken(String token){
        Optional<Reservation> reservationOptional = dao.findReservationsByToken(token);
        reservationOptional.orElseThrow(() -> new BadRequestException("Invalid token"));
        Reservation reservation = reservationOptional.get();

        if(reservation.getDate().isBefore(LocalDate.now())) throw new ValidationException("Nelze mazat rezervace v minulosti");
        if(reservation.getDate().equals(LocalDate.now()) && reservation.getFromToTime().getFrom().isBefore(LocalTime.now()))
            throw new ValidationException("Nelze mazat rezervace v minulosti");

        this.remove(reservation);
    }


}
