package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.CyclicReservation;
import cz.cvut.fel.tk21.model.Reservation;
import cz.cvut.fel.tk21.model.TennisCourt;
import cz.cvut.fel.tk21.rest.dto.reservation.CreateCyclicReservationDto;
import cz.cvut.fel.tk21.rest.dto.reservation.CyclicReservationReport;
import cz.cvut.fel.tk21.rest.dto.reservation.ReservationDto;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.CourtService;
import cz.cvut.fel.tk21.service.CyclicReservationService;
import cz.cvut.fel.tk21.service.ReservationService;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import cz.cvut.fel.tk21.ws.WebsocketService;
import cz.cvut.fel.tk21.ws.dto.UpdateReservationMessage;
import cz.cvut.fel.tk21.ws.dto.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/reservation/cyclic")
public class CyclicReservationController {

    private final RequestBodyValidator validator;
    private final CourtService courtService;
    private final CyclicReservationService cyclicReservationService;
    private final ReservationService reservationService;
    private final ClubService clubService;
    private final WebsocketService websocketService;

    @Autowired
    public CyclicReservationController(RequestBodyValidator validator, CourtService courtService, CyclicReservationService cyclicReservationService, ReservationService reservationService, ClubService clubService, WebsocketService websocketService) {
        this.validator = validator;
        this.courtService = courtService;
        this.cyclicReservationService = cyclicReservationService;
        this.reservationService = reservationService;
        this.clubService = clubService;
        this.websocketService = websocketService;
    }

    @RequestMapping(value = "/club/{club_id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CyclicReservationReport createCyclicReservation(@PathVariable("club_id") Integer club_id, @RequestParam(required = false, defaultValue = "1000000") Integer limit, @RequestBody CreateCyclicReservationDto dto){
        validator.validate(dto);
        if(dto.getDaysInBetween() < 1) throw new BadRequestException("Mezi rezervacemi musí být alespoň jeden den");

        Optional<Club> club = clubService.find(club_id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        Optional<TennisCourt> tennisCourt = courtService.findCourtInClub(club.get(), dto.getCourtId());
        tennisCourt.orElseThrow(() -> new NotFoundException("Tenisový kurt nebyl nalezen"));

        CyclicReservation cyclicReservation = cyclicReservationService.createCyclicReservation(dto.getDaysInBetween());
        CyclicReservationReport report =  cyclicReservationService.createReservationsBasedOnCyclicReservation(cyclicReservation, dto, club.get(), dto.getDate(), limit);
        report.setId(cyclicReservation.getId());

        List<Reservation> createdReservations = cyclicReservationService.findAllReservationsByCyclicID(cyclicReservation.getId());

        //Websocket messages for subscribers
        for(Reservation reservation : createdReservations){
            String formattedDate = reservation.getDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
            String destination = "/topic/reservation/" + reservation.getClub().getId() + "/" + formattedDate;
            websocketService.sendUpdateMessageToSubscribers(destination, reservation, UpdateType.CREATE);
        }

        return report;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ReservationDto> getCyclicReservationById(@PathVariable("id") Integer id){
        Optional<CyclicReservation> cyclicReservationOptional = cyclicReservationService.find(id);
        cyclicReservationOptional.orElseThrow(() -> new NotFoundException("Rezervace nebyla nalezena"));
        CyclicReservation cyclicReservation = cyclicReservationOptional.get();

        return cyclicReservationService.findAllReservationsByCyclicID(cyclicReservation.getId())
                .stream()
                .map(r -> new ReservationDto(r, reservationService.isCurrentUserAllowedToEditReservation(r), reservationService.isMine(r)))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCyclicReservationById(@PathVariable("id") Integer id){
        Optional<CyclicReservation> cyclicReservationOptional = cyclicReservationService.find(id);
        cyclicReservationOptional.orElseThrow(() -> new NotFoundException("Rezervace nebyla nalezena"));
        CyclicReservation cyclicReservation = cyclicReservationOptional.get();

        List<Reservation> reservations = cyclicReservationService.deleteCyclicReservation(cyclicReservation);

        //Websocket messages for subscribers
        for(Reservation reservation : reservations){
            String formattedDate = reservation.getDate().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
            String destination = "/topic/reservation/" + reservation.getClub().getId() + "/" + formattedDate;
            websocketService.sendUpdateMessageToSubscribers(destination, reservation, UpdateType.DELETE);
        }

        return ResponseEntity.noContent().build();
    }


}
