package cz.cvut.fel.tk21.ws;

import cz.cvut.fel.tk21.model.Reservation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.service.ReservationService;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.util.StringUtils;
import cz.cvut.fel.tk21.ws.dto.UpdateReservationMessage;
import cz.cvut.fel.tk21.ws.dto.UpdateType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class WebsocketService {

    private final UserService userService;
    private final SimpMessagingTemplate template;
    private final SimpUserRegistry simpUserRegistry;
    private final ReservationService reservationService;

    @Autowired
    public WebsocketService(UserService userService, SimpMessagingTemplate template, SimpUserRegistry simpUserRegistry, ReservationService reservationService) {
        this.userService = userService;
        this.template = template;
        this.simpUserRegistry = simpUserRegistry;
        this.reservationService = reservationService;
    }

    public void sendUpdateMessageToSubscribers(String destination, Reservation reservation, UpdateType updateType){
        Set<SimpSubscription> subscriptions = simpUserRegistry.findSubscriptions(simpSubscription -> simpSubscription.getDestination().equals("/user" + destination));
        for(SimpSubscription simpSubscription : subscriptions) {
            boolean editable = false;
            if(updateType != UpdateType.DELETE){
                editable = this.isReservationEditable(reservation, simpSubscription.getSession().getUser());
            }
            this.template.convertAndSendToUser(simpSubscription.getSession().getUser().getName(), destination, new UpdateReservationMessage(updateType, reservation, editable));
        }
    }

    private boolean isReservationEditable(Reservation reservation, SimpUser simpUser){
        User user = null;
        if(StringUtils.isValidEmail(simpUser.getName())){
            Optional<User> userOptional = userService.findUserByEmail(simpUser.getName());
            if(userOptional.isPresent()) user = userOptional.get();
        }
        return reservationService.isUserAllowedToEditReservation(user, reservation);
    }

}
