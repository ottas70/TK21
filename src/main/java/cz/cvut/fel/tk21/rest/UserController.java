package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.security.UserDetails;
import cz.cvut.fel.tk21.rest.dto.Info;
import cz.cvut.fel.tk21.rest.dto.club.ClubRelationshipDto;
import cz.cvut.fel.tk21.rest.dto.user.UserDto;
import cz.cvut.fel.tk21.service.ClubRelationService;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

}
