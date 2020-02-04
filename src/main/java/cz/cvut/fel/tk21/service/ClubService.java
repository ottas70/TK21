package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.ClubDao;
import cz.cvut.fel.tk21.dao.ClubRelationDao;
import cz.cvut.fel.tk21.dao.UserDao;
import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.*;
import cz.cvut.fel.tk21.rest.dto.club.ClubDto;
import cz.cvut.fel.tk21.rest.dto.club.ClubRegistrationDto;
import cz.cvut.fel.tk21.rest.dto.club.ClubSearchDto;
import cz.cvut.fel.tk21.rest.dto.club.SpecialOpeningHoursDto;
import cz.cvut.fel.tk21.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class ClubService extends BaseService<ClubDao, Club> {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ClubRelationService clubRelationService;

    @Autowired
    private CourtService courtService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationService reservationService;

    protected ClubService(ClubDao dao) {
        super(dao);
    }

    @Transactional
    public Integer registerClub(ClubRegistrationDto clubDto){
        if (!dao.isNameUnique(clubDto.getName())) {
            throw new ValidationException("Klub s tímto jménem již existuje");
        }
        if (!dao.isAddressUnique(clubDto.getAddress().getEntity())) {
            throw new ValidationException("Klub s touto adresou již existuje");
        }

        Club club = clubDto.getEntity();

        //Initial opening hours
        club.setOpeningHours(getInitialOpeningHours());

        //Initial season
        club.setSeasons(getInitialSeason(DateUtils.getCurrentYear()));

        club.setReservationPermission(ReservationPermission.SIGNED);
        club.setMinReservationTime(15);
        club.setMaxReservationTime(180);
        dao.persist(club);

        //links signed in user with this club as admin
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(email == null) throw new ValidationException("Uživatel neexistuje");
        Optional<User> user = userDao.getUserByEmail(email);
        if(user.isEmpty()) throw new ValidationException("Uživatel neexistuje");

        clubRelationService.addUserToClub(club, user.get(), UserRole.ADMIN);

        return club.getId();
    }

    @Transactional
    public boolean isCurrentUserAllowedToManageThisClub(Club club){
        User user = userService.getCurrentUser();
        if(user == null) return false;
        return clubRelationService.hasRole(club, user, UserRole.ADMIN);
    }

    @Transactional
    public boolean isUserAllowedToManageThisClub(User user, Club club){
        if(user == null) return false;
        return clubRelationService.hasRole(club, user, UserRole.ADMIN);
    }

    @Transactional(readOnly = true)
    public ClubSearchDto findAllPaginated(int page, int size) {
        return searchForClubsByName("", page, size);
    }

    @Transactional
    public ClubSearchDto searchForClubsByName(String name, int page, int size){
        List<ClubDto> clubs = new ArrayList<>();
        for(Club club : dao.findClubsByName(name, page, size)){
            clubs.add(new ClubDto(club, this.isCurrentUserAllowedToManageThisClub(club), reservationService.isCurrentUserAllowedToCreateReservation(club)));
        }
        int lastPage = (int) Math.ceil(dao.countClubsByName(name) / (double)size);
        return new ClubSearchDto(clubs, page, lastPage);
    }

    @Transactional
    public void addCourt(Club club, TennisCourt tennisCourt){
        if(!this.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");
        if(!courtService.isNameUniqueInClub(club, tennisCourt.getName())) throw new ValidationException("Kurt s tímto jménem již existuje");
        club.addCourt(tennisCourt);
        this.update(club);
    }

    @Transactional
    public void removeCourt(Club club, TennisCourt tennisCourt){
        if(!this.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");
        club.removeCourt(tennisCourt);
        this.update(club);
    }

    @Transactional
    public void addSeason(Club club, Season season, int year){
        if(!this.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");
        if(season.getSummer().getFrom().getYear() != year || season.getWinter().getFrom().getYear() != year)
            throw new BadRequestException("Datumy nesedí");
        club.addSeasonInYear(year, season);
        this.update(club);
    }

    @Transactional
    public void updateSeason(Club club, Season season, int year){
        if(!this.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");
        if(season.getSummer().getFrom().getYear() != year || season.getWinter().getFrom().getYear() != year)
            throw new BadRequestException("Datumy nesedí");
        club.addSeasonInYear(year, season);
        this.update(club);
    }

    @Transactional
    public void updateReservationPermission(Club club, ReservationPermission reservationPermission){
        if(!this.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");
        club.setReservationPermission(reservationPermission);
        this.update(club);
    }

    @Transactional
    public void updateMinReservationTime(Club club, int time){
        if(!this.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");
        if(time < 0) time = 0;
        club.setMinReservationTime(time);
        this.update(club);
    }

    @Transactional
    public void updateMaxReservationTime(Club club, int time){
        if(!this.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");
        if(time < 0) time = 0;
        club.setMaxReservationTime(time);
        this.update(club);
    }

    @Transactional
    public void updateName(Club club, String name){
        if(!this.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");
        club.setName(name);
        this.update(club);
    }

    @Transactional
    public void updateDescription(Club club, String description){
        if(!this.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");
        club.setDescription(description);
        this.update(club);
    }

    private OpeningHours getInitialOpeningHours(){
        OpeningHours openingHours = new OpeningHours();

        Map<Day, FromToTime> hoursMap = new HashMap<>();
        hoursMap.put(Day.MONDAY, new FromToTime("09:00", "21:00"));
        hoursMap.put(Day.TUESDAY, new FromToTime("09:00", "21:00"));
        hoursMap.put(Day.WEDNESDAY, new FromToTime("09:00", "21:00"));
        hoursMap.put(Day.THURSDAY, new FromToTime("09:00", "21:00"));
        hoursMap.put(Day.FRIDAY, new FromToTime("09:00", "21:00"));
        hoursMap.put(Day.SATURDAY, new FromToTime("09:00", "21:00"));
        hoursMap.put(Day.SUNDAY, new FromToTime("09:00", "21:00"));
        openingHours.setOpeningHours(hoursMap);

        return openingHours;
    }

    private Map<Integer, Season> getInitialSeason(int year){
        Map<Integer, Season> seasons = new HashMap<>();

        Season season = new Season(new FromToDate("03-01-" + year, "09-30-" + year), new FromToDate("10-01-" + year, "02-30-" + (year+1)));
        seasons.put(year, season);

        return seasons;
    }

}
