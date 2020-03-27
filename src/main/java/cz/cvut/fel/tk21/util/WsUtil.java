package cz.cvut.fel.tk21.util;

import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.security.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.WebSocketSession;

public class WsUtil {

    public static User extractUserFromSession(WebSocketSession session){
        if(session.getPrincipal() == null) return null;
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) session.getPrincipal();
        UserDetails userDetails = (UserDetails) token.getPrincipal();
        return userDetails.getUser();
    }

}
