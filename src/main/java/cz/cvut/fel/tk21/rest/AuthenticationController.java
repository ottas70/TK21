package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.InvalidCredentialsException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.security.AuthenticationRequest;
import cz.cvut.fel.tk21.rest.dto.Info;
import cz.cvut.fel.tk21.rest.dto.user.UserResponseDto;
import cz.cvut.fel.tk21.scraping.WebScraper;
import cz.cvut.fel.tk21.service.ClubRelationService;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.service.security.UserDetailsService;
import cz.cvut.fel.tk21.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ClubRelationService clubRelationService;

    @Autowired
    private WebScraper webScraper;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, UserService userService, JwtUtil jwtUtil, ClubRelationService clubRelationService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.clubRelationService = clubRelationService;
    }

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

        final User user = userService.findUserByEmail(userDetails.getUsername()).get();

        Club rootClub = clubRelationService.findUsersRootClub(user);
        ClubRelation rootRelation = null;
        if(rootClub != null){
            Optional<ClubRelation> relationOptional = clubRelationService.findClubRelationByUserAndClub(user, rootClub);
            if(relationOptional.isPresent()) rootRelation = relationOptional.get();
        }

                HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Set-Cookie","Credentials=" + token + ";" +
                "HttpOnly=True;Path=/;Secure=True");

        return new ResponseEntity<>(new UserResponseDto(user, rootRelation), responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity<?> logout() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Set-Cookie","Credentials=" + "" + ";" +
                "Max-Age=0;HttpOnly=True;Path=/;Secure=True");

        return ResponseEntity.noContent().headers(responseHeaders).build();
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.GET)
    public ResponseEntity<?> confirmEmail(@RequestParam("token")String token) {
        if(userService.isEmailTokenValid(token)){
            return ResponseEntity.ok(new Info("Email byl úspěšně ověřen"));
        }
        return ResponseEntity.badRequest().build();
    }

    //TODO remove
    @RequestMapping(value="/scrape", method = RequestMethod.GET)
    public ResponseEntity<?> scrape() {
        Runnable runnable = () -> {
          webScraper.scrapeCzTenis();
        };
        Thread thread = new Thread(runnable);
        thread.start();

        return ResponseEntity.ok().build();
    }

}
