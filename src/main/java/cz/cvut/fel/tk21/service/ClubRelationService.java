package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.ClubRelationDao;
import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClubRelationService extends BaseService<ClubRelationDao, ClubRelation> {

    @Autowired
    private UserService userService;

    @Autowired
    private ClubService clubService;

    protected ClubRelationService(ClubRelationDao dao) {
        super(dao);
    }

    @Transactional
    public void addUserToClub(Club club, User user, UserRole userRole){
        ClubRelation relation = new ClubRelation();
        relation.setClub(club);
        relation.setUser(user);
        relation.addRole(userRole);
        this.persist(relation);
    }

    @Transactional(readOnly = true)
    public boolean hasRole(Club club, User user, UserRole userRole){
        return dao.hasRole(user, club, userRole);
    }

    @Transactional(readOnly = true)
    public boolean isMemberOf(Club club, User user){
        if(user == null) return false;
        return dao.hasRelationToThisClub(user, club);
    }

    @Transactional(readOnly = true)
    public boolean isCurrentUserMemberOf(Club club){
        User user = userService.getCurrentUser();
        if(user == null) return false;
        return isMemberOf(club, user);
    }

    @Transactional(readOnly = true)
    public List<ClubRelation> findAllRelationsByUser(User user){
        return dao.findAllRelationsByUser(user);
    }

    @Transactional(readOnly = true)
    public List<ClubRelation> findAllRelationsByClub(Club club){
        return dao.findAllRelationsByClub(club);
    }

    @Transactional
    public void addRole(Club club, User user, UserRole role){
        if(!clubService.isCurrentUserAllowedToManageThisClub(club)) throw new UnauthorizedException("Přístup odepřen");
        if(role == null) throw new BadRequestException("Špatný dotaz");

        Optional<ClubRelation> relationOptional = dao.findRelationByUserAndClub(user, club);
        relationOptional.orElseThrow(() -> new ValidationException("Uživatel není členem klubu"));
        ClubRelation relation = relationOptional.get();

        if(relation.getRoles().contains(role)) return;

        relation.addRole(role);
        this.update(relation);
    }

    @Transactional
    public void deleteRole(Club club, User user, UserRole role){
        if(!clubService.isCurrentUserAllowedToManageThisClub(club)) throw new UnauthorizedException("Přístup odepřen");
        if(role == null) throw new BadRequestException("Špatný dotaz");

        Optional<ClubRelation> relationOptional = dao.findRelationByUserAndClub(user, club);
        relationOptional.orElseThrow(() -> new ValidationException("Uživatel není členem klubu"));
        ClubRelation relation = relationOptional.get();

        if(!relation.getRoles().contains(role)) return;

        relation.removeRole(role);
        this.update(relation);
    }

}
