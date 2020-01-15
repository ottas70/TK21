package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Reservation;
import cz.cvut.fel.tk21.rest.dto.CreatedDto;
import cz.cvut.fel.tk21.rest.dto.club.ClubRegistrationDto;
import cz.cvut.fel.tk21.rest.dto.reservation.CreateReservationDto;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.ReservationService;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import cz.cvut.fel.tk21.ws.dto.UpdateReservationMessage;
import cz.cvut.fel.tk21.ws.dto.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("api/reservation")
public class ReservationController {

    @Autowired
    private RequestBodyValidator validator;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private SimpMessagingTemplate template;

    @RequestMapping(value = "/club/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createReservation(@PathVariable("id") Integer id, @RequestBody CreateReservationDto reservationDto) {
        validator.validate(reservationDto);

        Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        reservationService.createReservationFromDTO(reservationDto, club.get(), reservationDto.getDate());

        Optional<Reservation> reservation = reservationService.findReservationByCourtIdDateAndTime(reservationDto.getCourtId(), reservationDto.getDate(), reservationDto.getTime());
        reservation.orElseThrow(() -> new ValidationException("Nastala chyba při uložení rezervace"));

        //Websocket message for subscribers
        String formattedDate = reservationDto.getDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        String destination = "/topic/reservation/" + id + "/" + formattedDate;
        this.template.convertAndSend(destination, new UpdateReservationMessage(UpdateType.CREATE, reservation.get()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteReservation(@PathVariable("id") Integer id){
        Optional<Reservation> reservationOptional = reservationService.find(id);
        reservationOptional.orElseThrow(() -> new ValidationException("Rezervace nebyla nalezena"));
        Reservation reservation = reservationOptional.get();

        reservationService.deleteReservation(reservation);

        //Websocket message for subscribers
        String formattedDate = reservation.getDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        String destination = "/topic/reservation/" + reservation.getClub().getId() + "/" + formattedDate;
        this.template.convertAndSend(destination, new UpdateReservationMessage(UpdateType.DELETE, reservation));

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
