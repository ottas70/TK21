package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.ClubDao;
import cz.cvut.fel.tk21.dao.ClubRelationDao;
import cz.cvut.fel.tk21.dao.UserDao;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.UserRole;
import cz.cvut.fel.tk21.rest.dto.ClubDto;
import cz.cvut.fel.tk21.rest.dto.ClubRegistrationDto;
import cz.cvut.fel.tk21.rest.dto.ClubSearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

}
