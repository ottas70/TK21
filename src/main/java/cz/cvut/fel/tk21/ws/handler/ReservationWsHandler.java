package cz.cvut.fel.tk21.ws.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.security.UserDetails;
import cz.cvut.fel.tk21.util.WsUtil;
import cz.cvut.fel.tk21.ws.dto.helperDto.ClubDateDto;
import cz.cvut.fel.tk21.ws.dto.helperDto.UpdateType;
import cz.cvut.fel.tk21.ws.service.ReservationWsService;
import cz.cvut.fel.tk21.ws.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ReservationWsHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(ReservationWsHandler.class);
    private final String HEARTBEAT = "-h-";

    private final ObjectMapper mapper;
    private final Map<Integer, Map<LocalDate, List<WebSocketSession>>> subscriptions;
    private final List<WebSocketSession> sessions;
    private ReservationWsService reservationWsService;

    public ReservationWsHandler() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.subscriptions = new ConcurrentHashMap<>();
        this.sessions = new CopyOnWriteArrayList<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if(message.getPayload().equals(HEARTBEAT)){
            handleHeartbeat(session);
            return;
        }
        GeneralMessage value = mapper.readValue(message.getPayload(), GeneralMessage.class);
        switch (value.getType()){
            case "UPDATE":
                String json = mapper.writeValueAsString(value.getBody());
                UpdateMessageBody body = mapper.readValue(json, UpdateMessageBody.class);
                handleUpdate(session, body);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        unsubscribeAll(session);
        sessions.remove(session);
    }

    private void handleUpdate(WebSocketSession session, UpdateMessageBody body){
        try{
            ClubDateDto unsubscribe = body.getUnsubscribe();
            ClubDateDto subscribe = body.getSubscribe();
            if(unsubscribe != null){
                unsubscribe(session, unsubscribe.getDate(), unsubscribe.getClubId());
            }
            if(subscribe == null) throw new BadRequestException("Bad request");

            User user = WsUtil.extractUserFromSession(session);
            ReservationMessage message = reservationWsService.createInitialMessage(user, subscribe.getClubId(), subscribe.getDate());
            GeneralMessage dto = new GeneralMessage(UpdateType.INIT.toString(), message);
            subscribe(session, message.getDate(), subscribe.getClubId());
            sendMessageToSession(session, dto);
        } catch (RuntimeException ex){
            sendMessageToSession(session, ex.getMessage());
        }
    }

    private void handleHeartbeat(WebSocketSession session){
        try {
            session.sendMessage(new TextMessage(HEARTBEAT));
        } catch (IOException e) {
            logger.info("Failed to send heartbeat message to session because " + e.getMessage());
        }
    }

    public void sendMessageToSession(WebSocketSession session, Object message){
        try {
            String json = mapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            logger.info("Failed to send message to session because " + e.getMessage());
        }
    }

    private void subscribe(WebSocketSession session, LocalDate date, int clubId){
        if(date == null) return;
        subscriptions.computeIfAbsent(clubId, id -> new ConcurrentHashMap<>());
        Map<LocalDate, List<WebSocketSession>> clubSubscriptions = subscriptions.get(clubId);
        clubSubscriptions.computeIfAbsent(date , id -> new LinkedList<>());
        List<WebSocketSession> clubDateSubscriptions = clubSubscriptions.get(date);
        if(!clubDateSubscriptions.contains(session)){
            clubDateSubscriptions.add(session);
        }
    }

    private void unsubscribe(WebSocketSession session, LocalDate date, int clubId){
        if(date == null) return;
        subscriptions.computeIfAbsent(clubId, id -> new ConcurrentHashMap<>());
        Map<LocalDate, List<WebSocketSession>> clubSubscriptions = subscriptions.get(clubId);
        clubSubscriptions.computeIfAbsent(date , id -> new LinkedList<>());
        List<WebSocketSession> clubDateSubscriptions = clubSubscriptions.get(date);
        clubDateSubscriptions.remove(session);
    }

    private void unsubscribeAll(WebSocketSession session){
        for (Map.Entry<Integer, Map<LocalDate, List<WebSocketSession>>> clubEntry : subscriptions.entrySet()){
            for (Map.Entry<LocalDate, List<WebSocketSession>> dateEntry : clubEntry.getValue().entrySet()){
                List<WebSocketSession> sessions = dateEntry.getValue();
                sessions.remove(session);
            }
        }
    }

    public List<WebSocketSession> findAllSubscriptions(int clubId, LocalDate date){
        subscriptions.computeIfAbsent(clubId, id -> new ConcurrentHashMap<>());
        subscriptions.get(clubId).computeIfAbsent(date , id -> new LinkedList<>());
        return subscriptions.get(clubId).get(date);
    }

    @Autowired
    public void setReservationWsService(ReservationWsService reservationWsService) {
        this.reservationWsService = reservationWsService;
    }
}
