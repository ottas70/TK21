package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.security.UserDetails;
import cz.cvut.fel.tk21.rest.dto.Info;
import cz.cvut.fel.tk21.rest.dto.UserDto;
import cz.cvut.fel.tk21.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.UnknownHostException;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto user) {
        final int userId;
        try {
            userId = this.userService.createUser(user);
        } catch (UnknownHostException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new Info("Účet byl úspěšně vytvořen. Na uvedený email byl odeslán ověřovací link"));
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User login() {
        return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

}
