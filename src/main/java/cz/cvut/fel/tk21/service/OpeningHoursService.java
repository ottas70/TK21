package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.OpeningHoursDao;
import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Day;
import cz.cvut.fel.tk21.model.FromToTime;
import cz.cvut.fel.tk21.model.OpeningHours;
import cz.cvut.fel.tk21.rest.dto.club.SpecialOpeningHoursDto;
import cz.cvut.fel.tk21.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OpeningHoursService extends BaseService<OpeningHoursDao, OpeningHours> {

    @Autowired
    private ClubService clubService;

    protected OpeningHoursService(OpeningHoursDao dao) {
        super(dao);
    }

    @Transactional
    public void updateRegularOpeningHours(Map<Integer, FromToTime> openingHours, Club club){
        if(!clubService.isCurrentUserAllowedToManageThisClub(club)) throw new UnauthorizedException("Tento klub nemáte právo editovat");

        Map<Day, FromToTime> regular = new HashMap<>();
        openingHours.forEach((k,v) -> {
            if(!v.isValidOpeningHour()) throw new BadRequestException("Otevírací doba je ve špatném formátu");
            regular.put(Day.getDayFromCode(k), v);
        });

        club.getOpeningHours().setRegularHours(regular);
        clubService.update(club);
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
        if(!clubService.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");

        FromToTime fromToTime = new FromToTime(from, to);
        if(!fromToTime.isValidOpeningHour()) throw new BadRequestException("Otevírací doba je ve špatném formátu");

        club.getOpeningHours().updateSpecialDate(date, fromToTime);
        clubService.update(club);
    }

    @Transactional
    public void addSpecialOpeningHour(Club club, LocalDate date, LocalTime from, LocalTime to){
        if(!clubService.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");

        int currentYear = DateUtils.getCurrentYear();
        if(currentYear != date.getYear() && currentYear != date.getYear() + 1){
            throw new ValidationException("U tohoto roku nelze přidávat speciální otevírací dobu");
        }

        FromToTime fromToTime = new FromToTime(from, to);
        if(!fromToTime.isValidOpeningHour()) throw new BadRequestException("Otevírací doba je ve špatném formátu");

        club.getOpeningHours().addSpecialDate(date, fromToTime);
        clubService.update(club);
    }

    @Transactional
    public void removeSpecialOpeningHour(Club club, LocalDate date){
        if(!clubService.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");
        club.getOpeningHours().removeSpecialDate(date);
        clubService.update(club);
    }

}
