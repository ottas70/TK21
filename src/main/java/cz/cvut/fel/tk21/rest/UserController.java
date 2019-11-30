package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.security.UserDetails;
import cz.cvut.fel.tk21.rest.dto.Info;
import cz.cvut.fel.tk21.rest.dto.UserDto;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RequestBodyValidator validator;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto user, BindingResult bindingResult) {
        validator.validate(user);
        final int userId = this.userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Info("Účet byl úspěšně vytvořen. Na uvedený email byl odeslán ověřovací link"));
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User getMyDetails() {
        return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAll() {
        return userService.findAll();
    }

}
