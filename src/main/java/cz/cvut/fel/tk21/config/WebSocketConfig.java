package cz.cvut.fel.tk21.config;

import cz.cvut.fel.tk21.ws.handler.ReservationWsHandler;
import cz.cvut.fel.tk21.ws.handler.PlayerWsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ReservationWsHandler reservationWsHandler;
    private final PlayerWsHandler playerWsHandler;

    @Autowired
    public WebSocketConfig(ReservationWsHandler reservationWsHandler, PlayerWsHandler playerWsHandler) {
        this.reservationWsHandler = reservationWsHandler;
        this.playerWsHandler = playerWsHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //TODO remove origin
        webSocketHandlerRegistry.addHandler(reservationWsHandler, "/websocket/reservation")
                .setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(playerWsHandler, "/websocket/player")
                .setAllowedOrigins("*");
    }

}
