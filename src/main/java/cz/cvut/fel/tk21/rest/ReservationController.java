package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Reservation;
import cz.cvut.fel.tk21.rest.dto.reservation.CreateReservationDto;
import cz.cvut.fel.tk21.rest.dto.reservation.ReservationDto;
import cz.cvut.fel.tk21.rest.dto.reservation.UpdateReservationDto;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.ReservationService;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import cz.cvut.fel.tk21.ws.WebsocketService;
import cz.cvut.fel.tk21.ws.dto.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/reservation")
public class ReservationController {

    private final RequestBodyValidator validator;
    private final ReservationService reservationService;
    private final ClubService clubService;
    private final WebsocketService websocketService;

    @Autowired
    public ReservationController(RequestBodyValidator validator, ReservationService reservationService, ClubService clubService, WebsocketService websocketService) {
        this.validator = validator;
        this.reservationService = reservationService;
        this.clubService = clubService;
        this.websocketService = websocketService;
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

        reservationService.createReservationFromDTO(reservationDto, club.get(), reservationDto.getDate());

        Optional<Reservation> reservation = reservationService.findReservationByCourtIdDateAndTime(reservationDto.getCourtId(), reservationDto.getDate(), reservationDto.getTime());
        reservation.orElseThrow(() -> new ValidationException("Nastala chyba při uložení rezervace"));


        //Websocket message for subscribers
        String formattedDate = reservationDto.getDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        String destination = "/topic/reservation/" + id + "/" + formattedDate;
        websocketService.sendUpdateMessageToSubscribers(destination, reservation.get(), UpdateType.CREATE);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ReservationDto(reservation.get(), reservationService.isCurrentUserAllowedToEditReservation(reservation.get()), reservationService.isMine(reservation.get())));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateReservation(@PathVariable("id") Integer id, @RequestBody UpdateReservationDto reservationDto){
        validator.validate(reservationDto);

        Optional<Reservation> reservationOptional = reservationService.find(id);
        reservationOptional.orElseThrow(() -> new NotFoundException("Rezervace nebyla nalezena"));
        Reservation reservation = reservationOptional.get();

        reservationService.updateReservation(reservation, reservationDto);

        //Websocket message for subscribers
        String formattedDate = reservationDto.getDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        String destination = "/topic/reservation/" + reservation.getClub().getId() + "/" + formattedDate;
        websocketService.sendUpdateMessageToSubscribers(destination, reservation, UpdateType.UPDATE);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteReservation(@PathVariable("id") Integer id){
        Optional<Reservation> reservationOptional = reservationService.find(id);
        reservationOptional.orElseThrow(() -> new NotFoundException("Rezervace nebyla nalezena"));
        Reservation reservation = reservationOptional.get();

        reservationService.deleteReservation(reservation);

        //Websocket message for subscribers
        String formattedDate = reservation.getDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        String destination = "/topic/reservation/" + reservation.getClub().getId() + "/" + formattedDate;
        websocketService.sendUpdateMessageToSubscribers(destination, reservation, UpdateType.DELETE);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteReservationByToken(@RequestParam("token")String token) {
        reservationService.deleteReservationByToken(token);
        return ResponseEntity.noContent().build();
    }

}
