package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.InvalidCredentialsException;
import cz.cvut.fel.tk21.model.security.AuthenticationRequest;
import cz.cvut.fel.tk21.model.security.AuthenticationResponse;
import cz.cvut.fel.tk21.rest.dto.Info;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.service.security.UserDetailsService;
import cz.cvut.fel.tk21.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;

@RestController
@RequestMapping("api")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(), authenticationRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Nesprávné přihlašovací údaje");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtUtil.generateToken(userDetails);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Set-Cookie","Credentials=" + token + ";" +
                "Max-Age=3600;HttpOnly=True");

        return ResponseEntity.noContent().headers(responseHeaders).build();
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<?> logout() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Set-Cookie","Credentials=" + "" + ";" +
                "Max-Age=0;HttpOnly=True");

        return ResponseEntity.noContent().headers(responseHeaders).build();
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.GET)
    public ResponseEntity<?> confirmEmail(@RequestParam("token")String token) {
        if(userService.isEmailTokenValid(token)){
            return ResponseEntity.ok(new Info("Email byl úspěšně ověřen"));
        }
        return ResponseEntity.badRequest().build();
    }



}
