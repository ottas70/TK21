package cz.cvut.fel.tk21.rest;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
public class WebSocketController {

    @MessageMapping("/ws/day")
    @SendTo("/topic/reservation")
    public String trial(String hello){
        return hello + LocalDate.now().toString();
    }

}
