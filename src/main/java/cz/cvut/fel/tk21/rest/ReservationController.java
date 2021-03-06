package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Reservation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.rest.dto.reservation.CreateReservationDto;
import cz.cvut.fel.tk21.rest.dto.reservation.ReservationDto;
import cz.cvut.fel.tk21.rest.dto.reservation.UpdateReservationDto;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.ReservationService;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import cz.cvut.fel.tk21.ws.service.ReservationWsService;
import cz.cvut.fel.tk21.ws.dto.helperDto.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/reservation")
public class ReservationController {

    private final RequestBodyValidator validator;
    private final ReservationService reservationService;
    private final ClubService clubService;
    private final ReservationWsService reservationWsService;
    private final UserService userService;

    @Autowired
    public ReservationController(RequestBodyValidator validator, ReservationService reservationService, ClubService clubService, ReservationWsService reservationWsService, UserService userService) {
        this.validator = validator;
        this.reservationService = reservationService;
        this.clubService = clubService;
        this.reservationWsService = reservationWsService;
        this.userService = userService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ReservationDto getReservationByID(@PathVariable("id") Integer id){
        Optional<Reservation> reservationOptional = reservationService.find(id);
        reservationOptional.orElseThrow(() -> new NotFoundException("Rezervace nebyla nalezena"));
        Reservation reservation = reservationOptional.get();

        return new ReservationDto(reservation, reservationService.isCurrentUserAllowedToEditReservation(reservation), reservationService.isMine(reservation));
    }

    @RequestMapping(value = "/club/{clubId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ReservationDto> getAllReservationByClubAndDate(@PathVariable("clubId") Integer clubId, @RequestParam @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate date){
        Optional<Club> club = clubService.find(clubId);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        List<Reservation> reservations = reservationService.findAllReservationsByClubAndDate(club.get(), date);
        return reservations.stream()
                .map(r -> new ReservationDto(r, reservationService.isCurrentUserAllowedToEditReservation(r), reservationService.isMine(r)))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/club/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReservationDto> createReservation(@PathVariable("id") Integer id, @RequestBody CreateReservationDto reservationDto) {
        validator.validate(reservationDto);

        Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        Reservation reservation = reservationService.createReservationFromDTO(reservationDto, club.get(), reservationDto.getDate());

        //Websocket message for subscribers
        reservationWsService.sendUpdateMessageToSubscribers(id, reservationDto.getDate(), reservation, UpdateType.CREATE);

        User currentUser = userService.getCurrentUser();
        if(currentUser != null){
            reservationService.sendReservationSummaryRegisteredPlayerEmail(reservation, currentUser, club.get());
        } else {
            reservationService.sendReservationSummaryNonRegisteredPlayerEmail(reservation, club.get());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new ReservationDto(reservation, reservationService.isCurrentUserAllowedToEditReservation(reservation), reservationService.isMine(reservation)));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateReservation(@PathVariable("id") Integer id, @RequestBody UpdateReservationDto reservationDto){
        validator.validate(reservationDto);

        Optional<Reservation> reservationOptional = reservationService.find(id);
        reservationOptional.orElseThrow(() -> new NotFoundException("Rezervace nebyla nalezena"));
        Reservation reservation = reservationOptional.get();

        reservationService.updateReservation(reservation, reservationDto);

        //Websocket message for subscribers
        reservationWsService.sendUpdateMessageToSubscribers(reservation.getClub().getId(), reservationDto.getDate(), reservation, UpdateType.UPDATE);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteReservation(@PathVariable("id") Integer id){
        Optional<Reservation> reservationOptional = reservationService.find(id);
        reservationOptional.orElseThrow(() -> new NotFoundException("Rezervace nebyla nalezena"));
        Reservation reservation = reservationOptional.get();

        reservationService.deleteReservation(reservation);

        //Websocket message for subscribers
        reservationWsService.sendUpdateMessageToSubscribers(reservation.getClub().getId(), reservation.getDate(), reservation, UpdateType.DELETE);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteReservationByToken(@RequestParam("token")String token) {
        Reservation reservation = reservationService.deleteReservationByToken(token);

        //Websocket message for subscribers
        reservationWsService.sendUpdateMessageToSubscribers(reservation.getClub().getId(), reservation.getDate(), reservation, UpdateType.DELETE);

        return ResponseEntity.noContent().build();
    }

}
