package cz.cvut.fel.tk21.filter;

import cz.cvut.fel.tk21.rest.handler.RestAccessDeniedHandler;
import cz.cvut.fel.tk21.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public JwtRequestFilter(JwtUtil jwtUtil, RestAccessDeniedHandler accessDeniedHandler, RestAccessDeniedHandler accessDeniedHandler1) {
        this.jwtUtil = jwtUtil;
        this.accessDeniedHandler = accessDeniedHandler1;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        final Cookie authCookie = WebUtils.getCookie(httpServletRequest, "Credentials");

        String jwt = null;
        if (authCookie != null) {
            jwt = authCookie.getValue();
        }

        UserDetails userDetails = jwtUtil.retrieveUserDetails(jwt);

        if(userDetails != null){
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            String xsrfTokenClaim = jwtUtil.extractXsrfToken(jwt);
            String xsrfTokenHeader = httpServletRequest.getHeader("CSRF_TOKEN");

            if(xsrfTokenHeader == null || !xsrfTokenHeader.equals(xsrfTokenClaim)){
                throwAccessDenied(httpServletRequest, httpServletResponse);
                return;
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void throwAccessDenied(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        accessDeniedHandler.handle(request, response, new AccessDeniedException("Přístup odepřen"));
    }
}
