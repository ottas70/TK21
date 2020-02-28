package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.security.UserDetails;
import cz.cvut.fel.tk21.rest.dto.Info;
import cz.cvut.fel.tk21.rest.dto.club.ClubRelationshipDto;
import cz.cvut.fel.tk21.rest.dto.user.PasswordChangeDto;
import cz.cvut.fel.tk21.rest.dto.user.UserDto;
import cz.cvut.fel.tk21.service.ClubRelationService;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ClubRelationService clubRelationService;

    @Autowired
    private RequestBodyValidator validator;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody UserDto user) {
        validator.validate(user);
        final int userId = this.userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Info("Účet byl úspěšně vytvořen. Na uvedený email byl odeslán ověřovací link"));
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User getMyDetails() {
        return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    @RequestMapping(value = "/clubs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClubRelationshipDto> getAllMyClubs(){
        User user = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if(user == null) throw new UnauthorizedException("Přístup odepřen");

        List<ClubRelation> relations = clubRelationService.findAllRelationsByUser(user);
        return relations.stream().map(ClubRelationshipDto::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/properties/name", method = RequestMethod.PUT, consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> updateName(@RequestBody String name){
        if(name == null) throw new BadRequestException("Chybný požadavek");
        userService.updateName(name);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/properties/surname", method = RequestMethod.PUT, consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> updateSurname(@RequestBody String surname){
        if(surname == null) throw new BadRequestException("Chybný požadavek");
        userService.updateSurname(surname);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/properties/email", method = RequestMethod.PUT, consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<?> updateEmail(@RequestBody String email){
        if(email == null) throw new BadRequestException("Chybný požadavek");
        if(!validator.isEmailValid(email)) throw new BadRequestException("Email není validní");
        userService.updateEmail(email);

        //logout user
        HttpHeaders responseHeaders = new HttpHeaders();
        //TODO add secure flag when using HTTPS
        responseHeaders.add("Set-Cookie","Credentials=" + "" + ";" +
                "Max-Age=0;HttpOnly=True;Path=/");

        return ResponseEntity.noContent().headers(responseHeaders).build();
    }

    @RequestMapping(value = "/properties/password", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePassword(@RequestBody PasswordChangeDto dto){
        validator.validate(dto);

        userService.updatePassword(dto.getOldPass(), dto.getNewPass());
        return ResponseEntity.noContent().build();
    }

}
