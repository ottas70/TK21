package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.rest.dto.club.SpecialOpeningHoursDto;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/club/{id}/special-day/")
public class SpecialOpeningHoursController {

    @Autowired
    private ClubService clubService;

    @Autowired
    private RequestBodyValidator validator;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SpecialOpeningHoursDto getSpecialOpeningHour(@PathVariable("id") Integer id, @RequestParam @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate date){
        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        SpecialOpeningHoursDto result = clubService.getSpecialOpeningHourByDate(club.get(), date);
        if(result == null) throw new NotFoundException("Toto datum má normální otevírací dobu");

        return result;
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateSpecialOpeningHour(@PathVariable("id") Integer id, @RequestParam @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate date, @RequestBody SpecialOpeningHoursDto special){
        validator.validate(special);

        final Optional<Club> clubOptional = clubService.find(id);
        clubOptional.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));
        Club club = clubOptional.get();

        if(clubService.hasThisDayRegularOpeningHours(club, date)){
            clubService.addSpecialOpeningHour(club, date, special.getFrom(), special.getTo());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        clubService.updateSpecialOpeningHour(club, date, special.getFrom(), special.getTo());
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteSpecialOpeningHour(@PathVariable("id") Integer id, @RequestParam @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate date){
        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        if(clubService.hasThisDayRegularOpeningHours(club.get(), date)){
            return ResponseEntity.notFound().build();
        }

        clubService.removeSpecialOpeningHour(club.get(), date);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSpecialOpeningHour(@PathVariable("id") Integer id, @RequestParam @DateTimeFormat(pattern = "MM-dd-yyyy") LocalDate date, @RequestBody SpecialOpeningHoursDto special){
        validator.validate(special);

        final Optional<Club> clubOptional = clubService.find(id);
        clubOptional.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));
        Club club = clubOptional.get();

        if(!clubService.hasThisDayRegularOpeningHours(club, date)){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        clubService.addSpecialOpeningHour(club, date, special.getFrom(), special.getTo());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SpecialOpeningHoursDto> getAllSpecialOpeningHours(@PathVariable("id") Integer id){
        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        List<SpecialOpeningHoursDto> result = new ArrayList<>();
        club.get().getOpeningHours().getSpecialDays()
                .forEach((k,v) -> result.add(new SpecialOpeningHoursDto(k, v.getFrom(), v.getTo())));

        return result;
    }

}
