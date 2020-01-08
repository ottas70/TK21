package cz.cvut.fel.tk21.ws;

import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.rest.dto.reservation.ReservationDto;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.ReservationService;
import cz.cvut.fel.tk21.ws.dto.CreateReservationDto;
import cz.cvut.fel.tk21.ws.dto.DateDto;
import cz.cvut.fel.tk21.ws.dto.ReservationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class WebSocketController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ClubService clubService;


    //Kdyz neprijde datum, tak se najde nejbližší možná rezervace
    @MessageMapping("/ws/reservation/{clubId}")
    @SendToUser("/topic/reservation/{clubId}")
    public ReservationMessage initialMessage(@DestinationVariable Integer clubId, @Payload(required = false) DateDto date, Principal principal){
        Optional<Club> club = clubService.find(clubId);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        LocalDate myDate = null;
        if(date == null){
            myDate = reservationService.findNearestAvailableReservationDate(club.get());
        }else{
            myDate = date.getDate();
        }

        return reservationService.initialReservationMessage(club.get(), LocalDate.now());
    }

    @MessageMapping("/ws/reservation/{clubId}/{date}/create")
    @SendTo("/topic/reservation/{clubId}/{date}")
    public List<ReservationDto> createReservation(@DestinationVariable Integer clubId, @DestinationVariable @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate date, @Payload CreateReservationDto reservationDto){
        if(!date.equals(reservationDto.getDate())) throw new ValidationException("Datumy se neshodují");

        Optional<Club> club = clubService.find(clubId);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        reservationService.createReservationFromDTO(reservationDto);

        List<ReservationDto> reservations =
                reservationService.findAllReservationsByClubAndDate(club.get(), date)
                .stream()
                .map(ReservationDto::new)
                .collect(Collectors.toList());
        return reservations;
    }

    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public String handleException(Exception ex){
        return ex.getMessage();
    }

}
