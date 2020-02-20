package cz.cvut.fel.tk21.ws;

import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Reservation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.security.UserDetails;
import cz.cvut.fel.tk21.rest.dto.reservation.CreateReservationDto;
import cz.cvut.fel.tk21.scraping.WebScraper;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.ReservationService;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.ws.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

@Controller
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private UserService userService;


    //Kdyz neprijde datum, tak se najde nejbližší možná rezervace
    @MessageMapping("/ws/reservation/{clubId}")
    @SendToUser("/topic/reservation/{clubId}")
    public ReservationMessage initialMessage(@DestinationVariable Integer clubId, @Payload(required = false) DateDto date, Principal principal){
        User user = null;

        if(principal != null){
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) principal;
            user = extractUserFromPrincipal(auth);
        }

        Optional<Club> club = clubService.find(clubId);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        LocalDate myDate = null;
        if(date == null){
            myDate = reservationService.findNearestAvailableReservationDate(club.get());
        }else{
            myDate = date.getDate();
        }

        return reservationService.initialReservationMessage(club.get(), myDate, user);
    }

    @MessageExceptionHandler
    @SendToUser("/topic/error")
    public String handleException(Exception ex){
        ex.printStackTrace();
        return ex.getMessage();
    }

    private User extractUserFromPrincipal(UsernamePasswordAuthenticationToken auth){
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        return userDetails.getUser();
    }

}
