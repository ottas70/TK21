package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.security.UserDetails;
import cz.cvut.fel.tk21.rest.dto.Info;
import cz.cvut.fel.tk21.rest.dto.club.BasicClubInfoDto;
import cz.cvut.fel.tk21.rest.dto.club.ClubRelationshipDto;
import cz.cvut.fel.tk21.rest.dto.post.PostsWithClubPaginatedDto;
import cz.cvut.fel.tk21.rest.dto.teams.CompetitionDto;
import cz.cvut.fel.tk21.rest.dto.tournament.TournamentDto;
import cz.cvut.fel.tk21.rest.dto.user.PasswordChangeDto;
import cz.cvut.fel.tk21.rest.dto.user.UserCompetitionsDto;
import cz.cvut.fel.tk21.rest.dto.user.UserDto;
import cz.cvut.fel.tk21.rest.dto.user.UserResponseDto;
import cz.cvut.fel.tk21.service.*;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/user")
public class UserController {

    private static final String DEFAULT_SIZE_OF_PAGE = "20";

    private final UserService userService;
    private final ClubRelationService clubRelationService;
    private final RequestBodyValidator validator;
    private final ClubService clubService;
    private final PostService postService;
    private final TeamCompetitionService teamCompetitionService;
    private final TournamentService tournamentService;

    @Autowired
    public UserController(UserService userService, ClubRelationService clubRelationService, RequestBodyValidator validator, ClubService clubService, PostService postService, TeamCompetitionService teamCompetitionService, TournamentService tournamentService) {
        this.userService = userService;
        this.clubRelationService = clubRelationService;
        this.validator = validator;
        this.clubService = clubService;
        this.postService = postService;
        this.teamCompetitionService = teamCompetitionService;
        this.tournamentService = tournamentService;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody UserDto user) {
        validator.validate(user);
        final int userId = this.userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Info("Účet byl úspěšně vytvořen. Na uvedený email byl odeslán ověřovací link"));
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponseDto getMyDetails() {
        User user = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        Club rootClub = clubRelationService.findUsersRootClub(user);
        ClubRelation rootRelation = null;
        if(rootClub != null){
            Optional<ClubRelation> relationOptional = clubRelationService.findClubRelationByUserAndClub(user, rootClub);
            if(relationOptional.isPresent()) rootRelation = relationOptional.get();
        }

        return new UserResponseDto(user, rootRelation);
    }

    @RequestMapping(value = "/clubs", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ClubRelationshipDto> getAllMyClubs(){
        User user = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        if(user == null) throw new UnauthorizedException("Přístup odepřen");

        List<ClubRelation> relations = clubRelationService.findAllRelationsByUser(user);
        return relations.stream().map(ClubRelationshipDto::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/clubs/register", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BasicClubInfoDto> getAllClubICanRegister(){
        User user = userService.getCurrentUser();
        if(user == null) throw new UnauthorizedException("Přístup odepřen");

        List<Club> result = clubService.findAllClubsByContactEmail(user.getEmail());
        return result.stream()
                .filter(c -> !c.isRegistered())
                .map(BasicClubInfoDto::new)
                .collect(Collectors.toList());
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
        responseHeaders.add("Set-Cookie","Credentials=" + "" + ";" +
                "Max-Age=0;HttpOnly=True;Path=/;Secure=True");

        return ResponseEntity.noContent().headers(responseHeaders).build();
    }

    @RequestMapping(value = "/properties/password", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePassword(@RequestBody PasswordChangeDto dto){
        validator.validate(dto);

        userService.updatePassword(dto.getOldPass(), dto.getNewPass());
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "pinned/{club_id}", method = RequestMethod.PUT)
    public ResponseEntity<?> setRootClub(@PathVariable("club_id") Integer club_id){
        final Optional<Club> club = clubService.find(club_id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        clubRelationService.setRootClub(club.get());

        return ResponseEntity.noContent().build();
    }

    /* *********************************
     * USER WALL
     ********************************* */

    @RequestMapping(value = "/wall/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PostsWithClubPaginatedDto findUsersPostsInWall(
            @RequestParam(value="page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value="size", required = false, defaultValue = DEFAULT_SIZE_OF_PAGE) Integer size){
        if(size < 1) throw new BadRequestException("Size cannot be less than one");
        if(page < 1) page = 1;

        User user = userService.getCurrentUser();
        if(user == null) throw new UnauthorizedException("Přístup zamítnut");

        return postService.findPostsPaginatedForUser(user, page, size);
    }

    @RequestMapping(value = "/wall/competitions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserCompetitionsDto findUsersUpcomingCompetitions(){
        User user = userService.getCurrentUser();
        if(user == null) throw new UnauthorizedException("Přístup zamítnut");

        List<CompetitionDto> teamCompetitions = teamCompetitionService.getAllUpcomingTeamCompetitionsInCurrentYearForUser(user);
        List<TournamentDto> tournaments = tournamentService.findAllUpcomingTournamentsForUser(user)
                .stream().map(TournamentDto::new).collect(Collectors.toList());

        return new UserCompetitionsDto(teamCompetitions, tournaments);
    }

    /* *********************************
     * Competitions
     ********************************* */

    @RequestMapping(value = "/tournaments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TournamentDto> findUsersTournaments(){
        User user = userService.getCurrentUser();
        if(user == null) throw new UnauthorizedException("Přístup zamítnut");

        return tournamentService.findAllTournamentsByUser(user)
                .stream().map(TournamentDto::new).collect(Collectors.toList());
    }

    @RequestMapping(value = "/teamCompetitions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CompetitionDto> findUsersTeamCompetitions(){
        User user = userService.getCurrentUser();
        if(user == null) throw new UnauthorizedException("Přístup zamítnut");

        return teamCompetitionService.getAllTeamCompetitionsInCurrentYearByUser(user);
    }

}
