package cz.cvut.fel.tk21.util;

import cz.cvut.fel.tk21.service.security.UserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    private static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60 * 5; //5 hours

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        if (isTokenExpired(token)) return false;
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public UserDetails retrieveUserDetails(String jwt){
        String username = null;

        if(jwt != null && !jwt.equals("")){
            try{
                username = this.extractUsername(jwt);
            } catch (JwtException ex){
                username = null;
            }
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = null;
            try{
                userDetails = userDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException ex){
                userDetails = null;
            }
            if(userDetails != null && this.isTokenValid(jwt, userDetails)){
                return userDetails;
            }
        }

        return null;
    }

}
