package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.FromToTime;
import cz.cvut.fel.tk21.model.Season;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.rest.dto.*;
import cz.cvut.fel.tk21.rest.dto.club.*;
import cz.cvut.fel.tk21.rest.dto.club.settings.MaxReservationDto;
import cz.cvut.fel.tk21.rest.dto.club.settings.MinReservationDto;
import cz.cvut.fel.tk21.rest.dto.club.settings.ReservationPermissionDto;
import cz.cvut.fel.tk21.rest.dto.club.verification.UserVerificationDto;
import cz.cvut.fel.tk21.rest.dto.club.verification.VerificationRequestDto;
import cz.cvut.fel.tk21.service.*;
import cz.cvut.fel.tk21.util.DateUtils;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/club")
public class ClubController {

    private static final String DEFAULT_SIZE_OF_PAGE = "20";

    private final ClubService clubService;
    private final VerificationRequestService verificationRequestService;
    private final OpeningHoursService openingHoursService;
    private final ReservationService reservationService;
    private final UserService userService;
    private final ClubRelationService clubRelationService;
    private final RequestBodyValidator validator;

    public ClubController(ClubService clubService, VerificationRequestService verificationRequestService, OpeningHoursService openingHoursService, ReservationService reservationService, UserService userService, ClubRelationService clubRelationService, RequestBodyValidator validator) {
        this.clubService = clubService;
        this.verificationRequestService = verificationRequestService;
        this.openingHoursService = openingHoursService;
        this.reservationService = reservationService;
        this.userService = userService;
        this.clubRelationService = clubRelationService;
        this.validator = validator;
    }

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
        return new ClubDto(club.get(), clubService.isCurrentUserAllowedToManageThisClub(club.get()), reservationService.isCurrentUserAllowedToCreateReservation(club.get()));
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

    @RequestMapping(value = "/{id}/settings", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ClubSettingsDto getClubSettings(@PathVariable("id") Integer id, @RequestParam(value="year", required = false) Integer year){
        boolean isYearSet = true;
        if(year == null){
            isYearSet = false;
            year = DateUtils.getCurrentYear();
        }

        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));
        return new ClubSettingsDto(club.get(), year, isYearSet);
    }

    /* *********************************
     * OPENING HOURS
     ********************************* */

    @RequestMapping(value="/{id}/opening-hours", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer, FromToTime> getClubOpeningHours(@PathVariable("id") Integer id){
        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));
        Map<Integer, FromToTime> result = new HashMap<>();
        club.get().getOpeningHours().getOpeningHours().forEach((k,v) -> result.put(k.getCode(), v));
        return result;
    }

    @RequestMapping(value = "/{id}/opening-hours", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateOpeningHours(@PathVariable("id") Integer id, @RequestBody Map<Integer, FromToTime> openingHours){
        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        openingHoursService.updateRegularOpeningHours(openingHours, club.get());

        return ResponseEntity.noContent().build();
    }

    /* *********************************
     * SEASONS
     ********************************* */

    @RequestMapping(value = "/{id}/seasons", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public SeasonDto getSeason(@PathVariable("id") Integer id, @RequestParam(value="year", required = false) Integer year){
        boolean isYearSet = true;
        if(year == null){
            year = DateUtils.getCurrentYear();
            isYearSet = false;
        }

        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        Season season = null;
        if(isYearSet){
            season = club.get().getSeasonInYear(year);
        }else{
            season = club.get().getSeasonByDate(LocalDate.now());
        }

        if(season == null) throw new NotFoundException("Sezóna neexistuje");

        return new SeasonDto(season);

    }

    @RequestMapping(value = "/{id}/seasons", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSeason(@PathVariable("id") Integer id, @RequestParam(value="year", required = false) Integer year, @RequestBody SeasonDto seasonDto){
        if(year == null) year = DateUtils.getCurrentYear();

        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        if(club.get().getSeasonInYear(year) != null) throw new ValidationException("Sezóna již existuje");

        clubService.updateSeason(club.get(), seasonDto.getEntity(), year);
        return ResponseEntity.noContent().build();

    }

    @RequestMapping(value = "/{id}/seasons", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateSeason(@PathVariable("id") Integer id, @RequestParam(value="year", required = false) Integer year, @RequestBody SeasonDto seasonDto){
        if(year == null) year = DateUtils.getCurrentYear();

        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        if(club.get().getSeasonInYear(year) == null){
            clubService.addSeason(club.get(), seasonDto.getEntity(), year);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        clubService.updateSeason(club.get(), seasonDto.getEntity(), year);
        return ResponseEntity.noContent().build();

    }

    /* *********************************
     * PROPERTIES
     ********************************* */

    @RequestMapping(value = "/{id}/properties/reservation/permission", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateReservationPermission(@PathVariable("id") Integer id, @RequestBody ReservationPermissionDto reservationPermissionDto){
        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        clubService.updateReservationPermission(club.get(), reservationPermissionDto.getReservationPermission());
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{id}/properties/reservation/min", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateMinReservationTime(@PathVariable("id") Integer id, @RequestBody MinReservationDto minReservationDto){
        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        clubService.updateMinReservationTime(club.get(), minReservationDto.getMinReservation());
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{id}/properties/reservation/max", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateMaxReservationTime(@PathVariable("id") Integer id, @RequestBody MaxReservationDto maxReservationDto){
        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        clubService.updateMaxReservationTime(club.get(), maxReservationDto.getMaxReservation());
        return ResponseEntity.noContent().build();
    }

    /* *********************************
     * USER VERIFICATION
     ********************************* */

    @RequestMapping(value = "/{id}/user-verification", method = RequestMethod.POST)
    public ResponseEntity<?> addMemberShip(@PathVariable("id") Integer id){
        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        verificationRequestService.addVerificationRequestToClub(club.get());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/{id}/verification-requests", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<VerificationRequestDto> getVerificationRequestsByClub(@PathVariable("id") Integer id){
        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        return verificationRequestService.findUnresolvedVerificationRequestsByClub(club.get())
                .stream()
                .map(VerificationRequestDto::new)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}/verify", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyUser(@PathVariable("id") Integer id, @RequestBody UserVerificationDto userVerificationDto){
        validator.validate(userVerificationDto);

        final Optional<Club> club = clubService.find(id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        final Optional<User> user = userService.find(userVerificationDto.getUserId());
        user.orElseThrow(() -> new NotFoundException("Uživatel nebyl nalezen"));

        boolean accepted = verificationRequestService.processVerification(club.get(), user.get(), userVerificationDto.getVerification());

        if (accepted) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }else{
            return ResponseEntity.noContent().build();
        }

    }

}
