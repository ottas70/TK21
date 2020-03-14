package cz.cvut.fel.tk21.config;

import cz.cvut.fel.tk21.ws.WebsocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebsocketHandler websocketHandler;

    @Autowired
    public WebSocketConfig(WebsocketHandler websocketHandler) {
        this.websocketHandler = websocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        //TODO remove origin
        webSocketHandlerRegistry.addHandler(websocketHandler, "/websocket")
                .setAllowedOrigins("*");
    }

}
