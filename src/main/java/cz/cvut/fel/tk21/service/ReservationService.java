package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.ReservationDao;
import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.*;
import cz.cvut.fel.tk21.model.mail.Mail;
import cz.cvut.fel.tk21.rest.dto.court.CourtDto;
import cz.cvut.fel.tk21.rest.dto.reservation.CreateReservationDto;
import cz.cvut.fel.tk21.rest.dto.reservation.ReservationDto;
import cz.cvut.fel.tk21.rest.dto.reservation.UpdateReservationDto;
import cz.cvut.fel.tk21.service.mail.MailService;
import cz.cvut.fel.tk21.ws.dto.helperDto.AvailableCourtDto;
import cz.cvut.fel.tk21.ws.dto.helperDto.CurrentSeasonDto;
import cz.cvut.fel.tk21.ws.dto.ReservationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    @Autowired
    private MailService mailService;

    protected ReservationService(ReservationDao dao) {
        super(dao);
    }

    @Transactional(readOnly = true)
    public ReservationMessage initialReservationMessage(Club club, LocalDate date, User user){
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
                message.setSeason(new CurrentSeasonDto(seasonName, seasonDates, this.isUserEnabledToCreateReservations(user, club, season, date)));
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
        message.setMember(clubRelationService.isMemberOf(club, user));
        message.setAuthorized(isUserAllowedToCreateReservation(user, club));
        message.setAllowedToCreateCyclicRes(isUserAllowedToCreateCyclicReservation(club, user));

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
        LocalTime now = LocalTime.now();
        LocalTime from = openingHours.getFrom();
        if(now.isAfter(from) && date.equals(LocalDate.now())) from = now.plusHours(1).withMinute(0).withSecond(0);
        List<TennisCourt> availableCourts = club.getAllAvailableCourts(date);
        if(availableCourts.isEmpty()) return null;
        while(from.plusHours(1).isBefore(openingHours.getTo()) || from.plusHours(1).equals(openingHours.getTo())){
            for (TennisCourt court : availableCourts){
                FromToTime reservationTime = new FromToTime(from, from.plusHours(1));
                if(courtService.isCourtAvailable(club, court, date, reservationTime)){
                    return new AvailableCourtDto(court.getId(), reservationTime);
                }
            }
            from = from.plusMinutes(15);
            if(from.getHour() == 0 && from.getMinute() == 0) return null;
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Reservation createReservationFromDTO(CreateReservationDto dto, Club club, LocalDate date){
        if(!club.isRegistered()) throw new ValidationException("Tento klub není registrován");
        if(!club.isReservationsEnabled()) throw new ValidationException("Tento klub nepodporuje rezervace");
        checkReservationPermission(club);
        if(club.getSeasonByDate(date) == null) throw new ValidationException("Na tento termín nelze kurt rezervovat.");
        if(!club.getSeasonByDate(date).isResEnabled(date) && !clubService.isCurrentUserAllowedToManageThisClub(club)) throw new ValidationException("V této sezóně nelze vytvářet rezervace");
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
        } else {
            Optional<User> userOptional = userService.findUserByEmail(reservation.getEmail());
            if(userOptional.isPresent()) throw new UnauthorizedException("USER EXISTS");

            String token = UUID.randomUUID().toString();
            while(dao.findReservationsByToken(token).isPresent()){
                token = UUID.randomUUID().toString();
            }
            reservation.setToken(token);
        }

        if(reservation.getEmail() == null || reservation.getName() == null || reservation.getSurname() == null) throw new ValidationException("Špatně vyplněné údaje.");
        if(reservation.getDuration() < club.getMinReservationTime() || (reservation.getDuration() > club.getMaxReservationTime()) && club.getMaxReservationTime() != 0 && !clubService.isUserAllowedToManageThisClub(currentUser, club)) throw new ValidationException("Trvání rezervace nevyhovuje požadavkům klubu.");

        Optional<TennisCourt> courtOptional = courtService.findCourtInClub(club, dto.getCourtId());
        courtOptional.orElseThrow(() -> new NotFoundException("Tenisový kurt nebyl nalezen."));
        TennisCourt court = courtOptional.get();

        reservation.setClub(club);
        reservation.setTennisCourt(court);
        reservation.setDate(date);
        reservation.setCyclicReservationId(-1);

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
        if(!club.isReservationsEnabled()) return false;
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
        if(reservation.getUser() == null) return clubService.isUserAllowedToManageThisClub(user, reservation.getClub());
        return reservation.getUser().getId() == user.getId() || clubService.isUserAllowedToManageThisClub(user, reservation.getClub());
    }

    @Transactional
    public boolean isUserEnabledToCreateReservations(User user, Club club, Season season, LocalDate date){
        if(clubService.isUserAllowedToManageThisClub(user, club)) return true;
        return season.isResEnabled(date);
    }

    @Transactional
    public boolean isMine(Reservation reservation){
        return isOwner(reservation, userService.getCurrentUser());
    }

    @Transactional
    public boolean isOwner(Reservation reservation, User user){
        if(user == null) return false;
        if(reservation.getUser() == null) return false;
        return reservation.getUser().getId() == user.getId();
    }

    @Transactional
    public boolean isUserAllowedToCreateCyclicReservation(Club club, User user){
        if(!club.isReservationsEnabled()) return false;
        if(user == null) return false;
        return clubService.isUserAllowedToManageThisClub(user, club);
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

    @Transactional(readOnly = true)
    public List<Reservation> findAllUpcomingUserReservations(User user){
        return dao.findAllUpcomingReservationsForUser(user);
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
    public Reservation deleteReservationByToken(String token){
        Optional<Reservation> reservationOptional = dao.findReservationsByToken(token);
        reservationOptional.orElseThrow(() -> new BadRequestException("Invalid token"));
        Reservation reservation = reservationOptional.get();

        if(reservation.getDate().isBefore(LocalDate.now())) throw new ValidationException("Nelze mazat rezervace v minulosti");
        if(reservation.getDate().equals(LocalDate.now()) && reservation.getFromToTime().getFrom().isBefore(LocalTime.now()))
            throw new ValidationException("Nelze mazat rezervace v minulosti");

        this.remove(reservation);

        return reservation;
    }

    public void sendReservationSummaryRegisteredPlayerEmail(Reservation reservation, User user, Club club){
        Mail mail = new Mail();
        mail.setFrom("noreply@tk21.cz");
        mail.setTo(user.getEmail());
        mail.setSubject("Rekapitulace rezervace");

        Map<String, Object> model = new HashMap<>();
        model.put("name", user.getName());
        model.put("surname", user.getSurname());
        model.put("clubID", club.getId());
        model.put("clubName", club.getName());
        model.put("courtName", reservation.getTennisCourt().getName());
        model.put("day", String.format("%02d", reservation.getDate().getDayOfMonth()));
        model.put("month", String.format("%02d", reservation.getDate().getMonthValue()));
        model.put("reservationYear", reservation.getDate().getYear());
        model.put("timeFrom", reservation.getFromToTime().getFrom().format(DateTimeFormatter.ofPattern("HH:mm")));
        model.put("timeTo", reservation.getFromToTime().getTo().format(DateTimeFormatter.ofPattern("HH:mm")));

        mail.setModel(model);

        mailService.sendReservationSummaryRegistered(mail);
    }

    public void sendReservationSummaryNonRegisteredPlayerEmail(Reservation reservation, Club club){
        Mail mail = new Mail();
        mail.setFrom("noreply@tk21.cz");
        mail.setTo(reservation.getEmail());
        mail.setSubject("Rekapitulace rezervace");

        Map<String, Object> model = new HashMap<>();
        model.put("name", reservation.getName());
        model.put("surname", reservation.getSurname());
        model.put("clubID", club.getId());
        model.put("clubName", club.getName());
        model.put("courtName", reservation.getTennisCourt().getName());
        model.put("day", String.format("%02d", reservation.getDate().getDayOfMonth()));
        model.put("month", String.format("%02d", reservation.getDate().getMonthValue()));
        model.put("reservationYear", reservation.getDate().getYear());
        model.put("timeFrom", reservation.getFromToTime().getFrom().format(DateTimeFormatter.ofPattern("HH:mm")));
        model.put("timeTo", reservation.getFromToTime().getTo().format(DateTimeFormatter.ofPattern("HH:mm")));
        model.put("token", reservation.getToken());
        model.put("clubEmail", club.getEmails().isEmpty() ? "Email nenalezen" : club.getEmails().stream().findFirst().get());

        mail.setModel(model);

        mailService.sendReservationSummaryNonRegistered(mail);
    }


}
