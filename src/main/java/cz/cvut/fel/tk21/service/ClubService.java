package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.ClubDao;
import cz.cvut.fel.tk21.dao.ClubRelationDao;
import cz.cvut.fel.tk21.dao.UserDao;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.*;
import cz.cvut.fel.tk21.rest.dto.club.ClubDto;
import cz.cvut.fel.tk21.rest.dto.club.ClubRegistrationDto;
import cz.cvut.fel.tk21.rest.dto.club.ClubSearchDto;
import cz.cvut.fel.tk21.rest.dto.club.SpecialOpeningHoursDto;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ClubRelationDao clubRelationDao;

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

        dao.persist(club);

        //links signed in user with this club as admin
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(email == null) throw new ValidationException("Uživatel neexistuje");
        Optional<User> user = userDao.getUserByEmail(email);
        if(user.isEmpty()) throw new ValidationException("Uživatel neexistuje");

        ClubRelation relation = new ClubRelation();
        relation.setClub(club);
        relation.setUser(user.get());
        relation.addRole(UserRole.ADMIN);
        clubRelationDao.persist(relation);

        return club.getId();
    }

    @Transactional
    public boolean isCurrentUserAllowedToManageThisClub(Club club){
        if (SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) return false;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userDao.getUserByEmail(email);
        if(user.isEmpty()) return false;
        User entity = user.get();
        for (ClubRelation relation : clubRelationDao.findAllRelationsByUser(entity)){
            if(relation.getClub().getId() == club.getId() && relation.getRoles().contains(UserRole.ADMIN)){
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public ClubSearchDto findAllPaginated(int page, int size) {
        return searchForClubsByName("", page, size);
    }

    @Transactional
    public ClubSearchDto searchForClubsByName(String name, int page, int size){
        List<ClubDto> clubs = new ArrayList<>();
        for(Club club : dao.findClubsByName(name, page, size)){
            clubs.add(new ClubDto(club, this.isCurrentUserAllowedToManageThisClub(club)));
        }
        int lastPage = (int) Math.ceil(dao.countClubsByName(name) / (double)size);
        return new ClubSearchDto(clubs, page, lastPage);
    }

    @Transactional
    public void updateRegularOpeningHours(Map<Integer, FromToTime> openingHours, Club club){
        if(!this.isCurrentUserAllowedToManageThisClub(club)) throw new UnauthorizedException("Tento klub nemáte právo editovat");

        Map<Day, FromToTime> regular = new HashMap<>();
        openingHours.forEach((k,v) -> regular.put(Day.getDayFromCode(k), v));

        club.getOpeningHours().setOpeningHours(regular);
        dao.update(club);
    }

    public SpecialOpeningHoursDto getSpecialOpeningHourByDate(Club club, LocalDate date){
        for (Map.Entry<LocalDate, FromToTime> entry : club.getOpeningHours().getSpecialDays().entrySet()) {
            if(entry.getKey().equals(date)){
                return new SpecialOpeningHoursDto(entry.getKey(), entry.getValue().getFrom(), entry.getValue().getTo());
            }
        }
        return null;
    }

    public boolean hasThisDayRegularOpeningHours(Club club, LocalDate date){
        return !club.getOpeningHours().containsSpecialDate(date);
    }

    @Transactional
    public void updateSpecialOpeningHour(Club club, LocalDate date, LocalTime from, LocalTime to){
        if(!this.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");
        club.getOpeningHours().updateSpecialDate(date, new FromToTime(from, to));
        this.update(club);
    }

    @Transactional
    public void addSpecialOpeningHour(Club club, LocalDate date, LocalTime from, LocalTime to){
        if(!this.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");
        club.getOpeningHours().addSpecialDate(date, new FromToTime(from, to));
        this.update(club);
    }

    @Transactional
    public void removeSpecialOpeningHour(Club club, LocalDate date){
        if(!this.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");
        club.getOpeningHours().removeSpecialDate(date);
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

}
