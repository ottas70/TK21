package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.TennisCourt;
import cz.cvut.fel.tk21.rest.dto.CreatedDto;
import cz.cvut.fel.tk21.rest.dto.club.ClubDto;
import cz.cvut.fel.tk21.rest.dto.club.CourtDto;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.CourtService;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/club")
public class CourtController {

    @Autowired
    private CourtService courtService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private RequestBodyValidator validator;

    @RequestMapping(value = "/court/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CourtDto getCourt(@PathVariable("id") Integer id){
        Optional<TennisCourt> court = courtService.find(id);
        court.orElseThrow(() -> new NotFoundException("Tenisový kurt nebyl nalezen"));
        return new CourtDto(court.get());
    }

    @RequestMapping(value = "/{id}/courts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CourtDto> getAllCourtsInClub(@PathVariable("id") Integer id){
        Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Tenisový klub nebyl nalezen"));
        List<TennisCourt> courts = courtService.findAllByClub(club.get());
        List<CourtDto> result = new ArrayList<>();
        courts.forEach(c -> result.add(new CourtDto(c)));
        return result;
    }

    @RequestMapping(value = "/{id}/courts", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCourt(@PathVariable("id") Integer id, @RequestBody CourtDto court){
        validator.validate(court);

        Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Tenisový klub nebyl nalezen"));

        clubService.addCourt(club.get(), court.getEntity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/court/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCourt(@PathVariable("id") Integer id, @RequestBody CourtDto courtDto){
        validator.validate(courtDto);

        Optional<TennisCourt> court = courtService.find(id);
        court.orElseThrow(() -> new NotFoundException("Tenisový kurt nebyl nalezen"));

        courtService.update(id, courtDto.getEntity(), court.get().getClub());
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/court/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCourt(@PathVariable("id") Integer id){
        Optional<TennisCourt> court = courtService.find(id);
        court.orElseThrow(() -> new NotFoundException("Tenisový kurt nebyl nalezen"));

        clubService.removeCourt(court.get().getClub(), court.get());
        return ResponseEntity.noContent().build();
    }

}
