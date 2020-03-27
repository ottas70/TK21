package cz.cvut.fel.tk21.ws.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.util.WsUtil;
import cz.cvut.fel.tk21.ws.dto.GeneralMessage;
import cz.cvut.fel.tk21.ws.dto.PlayerInfoMessageBody;
import cz.cvut.fel.tk21.ws.dto.helperDto.IdDto;
import cz.cvut.fel.tk21.ws.dto.helperDto.MessageDto;
import cz.cvut.fel.tk21.ws.dto.helperDto.PlayerInfoCzTenis;
import cz.cvut.fel.tk21.ws.service.PlayerWsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class PlayerWsHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(PlayerWsHandler.class);
    private final String HEARTBEAT = "-h-";

    private final ObjectMapper mapper;
    private final List<WebSocketSession> sessions;
    private final Map<WebSocketSession, List<PlayerInfoCzTenis>> requests;
    private PlayerWsService playerWsService;

    public PlayerWsHandler() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.sessions = new CopyOnWriteArrayList<>();
        this.requests = new ConcurrentHashMap<>();
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
            case "PLAYER_INFO":
                String player_info = mapper.writeValueAsString(value.getBody());
                PlayerInfoMessageBody body = mapper.readValue(player_info, PlayerInfoMessageBody.class);
                handlePlayerInfo(session, body);
                break;
            case "PLAYER_CONFIRMATION":
                String player_conf = mapper.writeValueAsString(value.getBody());
                IdDto idDto = mapper.readValue(player_conf, IdDto.class);
                handlePlayerConfirmation(session, idDto.getId());
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        requests.remove(session);
    }

    private void handleHeartbeat(WebSocketSession session){
        try {
            session.sendMessage(new TextMessage(HEARTBEAT));
        } catch (IOException e) {
            logger.info("Failed to send heartbeat message to session because " + e.getMessage());
        }
    }

    private void handlePlayerInfo(WebSocketSession session, PlayerInfoMessageBody body){
        try {
            Optional<Club> club = playerWsService.findClub(body.getClubId());
            club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));
            User user = WsUtil.extractUserFromSession(session);
            if(!playerWsService.isAuthorizedToRegisterPlayer(user, club.get())) throw new UnauthorizedException("Přístup odepřen");
            playerWsService.isAlreadyRegistered(body.getPlayerEmail(), club.get());

            List<PlayerInfoCzTenis> players = playerWsService.findPlayerOnCzTenis(body, club.get());
            requests.put(session, players);
            List<GeneralMessage> dtos = players.stream()
                    .map(p -> new GeneralMessage("PLAYER_RESULTS", p))
                    .collect(Collectors.toList());

            sendMessageToSession(session, dtos);
        } catch (RuntimeException | IOException ex){
            sendMessageToSession(session, ex.getMessage());
        }
    }

    private void handlePlayerConfirmation(WebSocketSession session, Long id){
        try {
            List<PlayerInfoCzTenis> players = requests.get(session);
            if(players == null || players.isEmpty()) throw new ValidationException("Musíte nejdříve zadat správné informace o hráči");
            Optional<PlayerInfoCzTenis> playerOptional = players.stream().filter(p -> p.getId() == id).findFirst();
            if(playerOptional.isEmpty()) throw new BadRequestException("Nesprávné ID hráče");
            PlayerInfoCzTenis player = playerOptional.get();

            String result = playerWsService.invitePlayer(player);

            sendMessageToSession(session, new GeneralMessage("RESULT", new MessageDto(result)));
        } catch (RuntimeException ex) {
            sendMessageToSession(session, ex.getMessage());
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

    @Autowired
    public void setPlayerWsService(PlayerWsService playerWsService) {
        this.playerWsService = playerWsService;
    }
}
