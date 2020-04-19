package cz.cvut.fel.tk21.filter;

import cz.cvut.fel.tk21.rest.handler.RestAccessDeniedHandler;
import cz.cvut.fel.tk21.scraping.WebScraper;
import cz.cvut.fel.tk21.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final JwtUtil jwtUtil;

    @Autowired
    public JwtRequestFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
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

            if(isCsrfTokenRequired(httpServletRequest)){
                String xsrfTokenClaim = jwtUtil.extractXsrfToken(jwt);
                //String xsrfTokenHeader = httpServletRequest.getHeader("CSRF_TOKEN");
                String xsrfTokenHeader = httpServletRequest.getHeader("abcd");

                if(xsrfTokenHeader == null || !xsrfTokenHeader.equals(xsrfTokenClaim)){
                    log.trace("Invalid CSRF token for user " + userDetails.getUsername());
                } else {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            } else {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private boolean isCsrfTokenRequired(HttpServletRequest request){
        return request.getRequestURI().startsWith("/api");
    }
}
