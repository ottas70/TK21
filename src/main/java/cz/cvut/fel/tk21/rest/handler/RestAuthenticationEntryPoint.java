package cz.cvut.fel.tk21.rest.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.tk21.model.security.ApiResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        Object response = null;
        if(e instanceof DisabledException){
            response = new VerifiedErrorInfo(401, "Email uživatele ještě nebyl ověřen");
        } else {
            response = new ApiResponse(401, "Přístup odepřen");
        }
        httpServletResponse.setStatus(401);
        httpServletResponse.setHeader("Content-Type", "application/json");
        OutputStream out = httpServletResponse.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, response);
        out.flush();
    }
}
