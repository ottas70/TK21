package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.CourtDao;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.TennisCourt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourtService extends BaseService<CourtDao, TennisCourt> {

    @Autowired
    private ClubService clubService;

    protected CourtService(CourtDao dao) {
        super(dao);
    }

    @Transactional
    public List<TennisCourt> findAllByClub(Club club){
        return dao.findAllCourtsByClub(club);
    }

    @Transactional
    public boolean isNameUniqueInClub(Club club, String name){
        return dao.isNameUniqueInClub(club, name);
    }

    @Transactional
    public void update(Integer id, TennisCourt entity, Club club) {
        if(!clubService.isCurrentUserAllowedToManageThisClub(club)) throw new UnauthorizedException("Přístup odepřen");

        Optional<TennisCourt> court = find(id);
        court.orElseThrow(() -> new NotFoundException("Tenisový kurt nebyl nalezen"));

        if(!court.get().getName().equals(entity.getName())){
            if(!isNameUniqueInClub(club, entity.getName())) throw new ValidationException("Kurt s tímto jménem již existuje");
        }

        court.get().setName(entity.getName());
        court.get().setSurfaceType(entity.getSurfaceType());
        court.get().setAvailableInWinter(entity.isAvailableInWinter());
        court.get().setAvailableInSummer(entity.isAvailableInSummer());
        dao.update(court.get());
    }
}
