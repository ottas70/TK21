package cz.cvut.fel.tk21.rest.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.tk21.model.security.ApiResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        ApiResponse response = new ApiResponse(403, "Přístup odepřen");
        httpServletResponse.setStatus(response.getStatus());
        httpServletResponse.setHeader("Content-Type", "application/json");
        OutputStream out = httpServletResponse.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, response);
        out.flush();
    }
}
