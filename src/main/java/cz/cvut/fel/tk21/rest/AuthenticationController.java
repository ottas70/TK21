package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.InvalidCredentialsException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.Invitation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.security.AuthenticationRequest;
import cz.cvut.fel.tk21.rest.dto.Info;
import cz.cvut.fel.tk21.rest.dto.user.EmailDto;
import cz.cvut.fel.tk21.rest.dto.user.NewPasswordDto;
import cz.cvut.fel.tk21.rest.dto.user.UserResponseDto;
import cz.cvut.fel.tk21.scraping.WebScraper;
import cz.cvut.fel.tk21.service.ClubRelationService;
import cz.cvut.fel.tk21.service.InvitationService;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.service.security.UserDetailsService;
import cz.cvut.fel.tk21.util.JwtUtil;
import cz.cvut.fel.tk21.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RestController
@RequestMapping("api")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ClubRelationService clubRelationService;
    private final InvitationService invitationService;

    @Autowired
    private WebScraper webScraper;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, UserService userService, JwtUtil jwtUtil, ClubRelationService clubRelationService, InvitationService invitationService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.clubRelationService = clubRelationService;
        this.invitationService = invitationService;
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) {
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
        final String token = jwtUtil.generateToken(userDetails, authenticationRequest.isSignOut());

        final User user = userService.findUserByEmail(userDetails.getUsername()).get();

        Club rootClub = clubRelationService.findUsersRootClub(user);
        ClubRelation rootRelation = null;
        if(rootClub != null){
            Optional<ClubRelation> relationOptional = clubRelationService.findClubRelationByUserAndClub(user, rootClub);
            if(relationOptional.isPresent()) rootRelation = relationOptional.get();
        }

        //Set cookie
        Cookie cookie = new Cookie("Credentials", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);
        if(!authenticationRequest.isSignOut()) cookie.setMaxAge(60 * 60 * 24 * 365);
        response.addCookie(cookie);

        return new ResponseEntity<>(new UserResponseDto(user, rootRelation), HttpStatus.OK);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("Credentials", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.GET)
    public ResponseEntity<?> confirmEmail(@RequestParam("token")String token) {
        if(userService.isEmailTokenValid(token)){
            return ResponseEntity.ok(new Info("Email byl úspěšně ověřen"));
        }
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/password/forget", method = RequestMethod.POST)
    public ResponseEntity<?> forgottenPassword(@RequestBody EmailDto dto){
        String email = dto.getEmail();
        if(!StringUtils.isValidEmail(email)) throw new BadRequestException("Nevalidní email");
        if(userService.getCurrentUser() != null) throw new ValidationException("Již přihlášen");

        Optional<User> user = userService.findUserByEmail(email);
        user.orElseThrow(() -> new ValidationException("Uživatel nenalezen"));

        userService.forgottenPassword(user.get());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/passwordChange/{token}", method = RequestMethod.POST)
    public ResponseEntity<?> confirmToken(@PathVariable("token") String token, @RequestBody NewPasswordDto newPasswordDto){
        Optional<Invitation> invitation = invitationService.findByConfirmationToken(token);
        invitation.orElseThrow(() -> new BadRequestException("Chybný dotaz"));

        userService.resetPassword(invitation.get(), newPasswordDto.getPassword());

        return ResponseEntity.ok().build();
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
