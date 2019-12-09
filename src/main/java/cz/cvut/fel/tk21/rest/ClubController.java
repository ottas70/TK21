package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.rest.dto.ClubDto;
import cz.cvut.fel.tk21.rest.dto.ClubRegistrationDto;
import cz.cvut.fel.tk21.rest.dto.ClubSearchDto;
import cz.cvut.fel.tk21.rest.dto.CreatedDto;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import cz.cvut.fel.tk21.util.RestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/club")
public class ClubController {

    private static final String DEFAULT_SIZE_OF_PAGE = "20";

    @Autowired
    private ClubService clubService;

    @Autowired
    private RequestBodyValidator validator;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerClub(@RequestBody ClubRegistrationDto club) {
        validator.validate(club);
        Integer clubId = clubService.registerClub(club);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreatedDto(clubId));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ClubDto getClub(@PathVariable("id") Integer id) {
        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));
        return new ClubDto(club.get(), clubService.isCurrentUserAllowedToManageThisClub(club.get()));
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ClubSearchDto getAllClubs(
            @RequestParam(value="page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value="size", required = false, defaultValue = DEFAULT_SIZE_OF_PAGE) Integer size){
        List<ClubDto> result = new ArrayList<>();
        if(size < 1) throw new BadRequestException("Size cannot be less than zero");
        if(page < 1) page = 1;
        return clubService.findAllPaginated(page,size);
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, params = {"name"})
    public ClubSearchDto searchForClubs(
            @RequestParam("name") String name,
            @RequestParam(value="page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value="size", required = false, defaultValue = DEFAULT_SIZE_OF_PAGE) Integer size){
        if(size < 1) throw new BadRequestException("Size cannot be less than zero");
        if(page < 1) page = 1;
        return clubService.searchForClubsByName(name, page, size);
    }

}
