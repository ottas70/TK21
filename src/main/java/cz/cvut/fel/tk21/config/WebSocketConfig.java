package cz.cvut.fel.tk21.config;

import cz.cvut.fel.tk21.ws.handler.ReservationWsHandler;
import cz.cvut.fel.tk21.ws.handler.PlayerWsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ReservationWsHandler reservationWsHandler;
    private final PlayerWsHandler playerWsHandler;

    @Value("${backend.allowCors}")
    private String allowCors;

    @Autowired
    public WebSocketConfig(ReservationWsHandler reservationWsHandler, PlayerWsHandler playerWsHandler) {
        this.reservationWsHandler = reservationWsHandler;
        this.playerWsHandler = playerWsHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        if(this.allowsCors()){
            webSocketHandlerRegistry
                    .addHandler(reservationWsHandler, "/websocket/reservation")
                    .setAllowedOrigins("*");
            webSocketHandlerRegistry
                    .addHandler(playerWsHandler, "/websocket/player")
                    .setAllowedOrigins("*");
        } else {
            webSocketHandlerRegistry.addHandler(reservationWsHandler, "/websocket/reservation");
            webSocketHandlerRegistry.addHandler(playerWsHandler, "/websocket/player");
        }
    }

    private boolean allowsCors() {
        return Boolean.parseBoolean(allowCors);
    }
}
