package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.rest.dto.ClubRegistrationDto;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import cz.cvut.fel.tk21.util.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/club")
public class ClubController {

    @Autowired
    private ClubService clubService;

    @Autowired
    private RequestBodyValidator validator;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerClub(@Valid @RequestBody ClubRegistrationDto club) {
        validator.validate(club);
        Integer clubId = clubService.registerClub(club);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", clubId);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

}
