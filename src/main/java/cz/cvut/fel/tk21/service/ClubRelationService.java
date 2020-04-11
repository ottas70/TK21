package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.annotation.ClubManagementOnly;
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
        if(user == null) return false;
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
    public void deleteAllRelationsByClub(Club club){
        dao.findAllRelationsByClub(club).forEach(this::remove);
    }

    @Transactional(readOnly = true)
    public Optional<ClubRelation> findClubRelationByUserAndClub(User user, Club club){
        return dao.findRelationByUserAndClub(user, club);
    }

    @Transactional(readOnly = true)
    public List<ClubRelation> findAllRelationsByClubAndRole(Club club, UserRole role){
        return dao.findAllRelationsByClubAndRole(club, role);
    }

    @Transactional(readOnly = true)
    public boolean hasRoleSomewhere(User user, UserRole role){
        return dao.hasRoleSomewhere(user, role);
    }

    @Transactional(readOnly = true)
    public List<ClubRelation> findAllRelationsWithRole(User user, UserRole role){
        return dao.findAllRelationsWithRole(user, role);
    }

    @Transactional
    @ClubManagementOnly
    public void addRole(Club club, User user, UserRole role){
        if(role == null) throw new BadRequestException("Špatný dotaz");

        Optional<ClubRelation> relationOptional = dao.findRelationByUserAndClub(user, club);
        relationOptional.orElseThrow(() -> new ValidationException("Uživatel není členem klubu"));
        ClubRelation relation = relationOptional.get();

        if(relation.getRoles().contains(role)) return;
        if(role == UserRole.ADMIN && relation.getRoles().contains(UserRole.EMPLOYEE)
                || role == UserRole.EMPLOYEE && relation.getRoles().contains(UserRole.ADMIN)
                || role == UserRole.PROFESSIONAL_PLAYER && relation.getRoles().contains(UserRole.RECREATIONAL_PLAYER)
                || role == UserRole.RECREATIONAL_PLAYER && relation.getRoles().contains(UserRole.PROFESSIONAL_PLAYER))
            throw new ValidationException("Tato role koliduje se zbylými rolemi");

        relation.addRole(role);
        this.update(relation);
    }

    @Transactional
    public void addRoleWithoutPermissionCheck(Club club, User user, UserRole role){
        if(role == null) throw new BadRequestException("Špatný dotaz");

        Optional<ClubRelation> relationOptional = dao.findRelationByUserAndClub(user, club);
        relationOptional.orElseThrow(() -> new ValidationException("Uživatel není členem klubu"));
        ClubRelation relation = relationOptional.get();

        if(relation.getRoles().contains(role)) return;

        relation.addRole(role);
        this.update(relation);
    }

    @Transactional
    @ClubManagementOnly
    public void deleteRole(Club club, User user, UserRole role, boolean isMyselfEditable){
        if(!isMyselfEditable){
            if(userService.getCurrentUser().getId() == user.getId()) throw new ValidationException("Nemůžete odebírat svoje role");
        }
        if(role == null) throw new BadRequestException("Špatný dotaz");

        Optional<ClubRelation> relationOptional = dao.findRelationByUserAndClub(user, club);
        relationOptional.orElseThrow(() -> new ValidationException("Uživatel není členem klubu"));
        ClubRelation relation = relationOptional.get();

        if(!relation.getRoles().contains(role)) return;

        relation.removeRole(role);

        this.update(relation);
    }

    @Transactional
    public void deleteRoleWithoutPermissionCheck(Club club, User user, UserRole role){
        if(role == null) throw new BadRequestException("Špatný dotaz");

        Optional<ClubRelation> relationOptional = dao.findRelationByUserAndClub(user, club);
        relationOptional.orElseThrow(() -> new ValidationException("Uživatel není členem klubu"));
        ClubRelation relation = relationOptional.get();

        if(!relation.getRoles().contains(role)) return;

        relation.removeRole(role);

        this.update(relation);
    }

    @Transactional
    @ClubManagementOnly
    public void removeMemberFromClub(Club club, User user){
        if(userService.getCurrentUser().getId() == user.getId()) throw new ValidationException("Nemůžete odebrát sám sebe");

        Optional<ClubRelation> relationOptional = dao.findRelationByUserAndClub(user, club);
        relationOptional.orElseThrow(() -> new ValidationException("Uživatel není členem klubu"));
        ClubRelation relation = relationOptional.get();
        if(relation.getRoles().contains(UserRole.ADMIN)) throw new ValidationException("Uživatele s rolí admin nelze odstranit");
        if(user.getRootClub() != null && user.getRootClub().getId() == club.getId()){
            user.setRootClub(null);
            userService.update(user);
        }

        this.remove(relation);
    }

    @Transactional
    public void quitClub(Club club){
        User user = userService.getCurrentUser();
        if(user == null) throw new BadRequestException("Uživatel musí být přihlášen");

        Optional<ClubRelation> relationOptional = dao.findRelationByUserAndClub(user, club);
        relationOptional.orElseThrow(() -> new ValidationException("Nejste členem klubu"));
        ClubRelation relation = relationOptional.get();
        if(relation.getRoles().contains(UserRole.ADMIN)) throw new ValidationException("ADMIN nemůže z klubu odejít");
        if(user.getRootClub() != null && user.getRootClub().getId() == club.getId()){
            user.setRootClub(null);
            userService.update(user);
        }

        this.remove(relation);
    }

    @Transactional
    public void setRootClub(Club club){
        if(!this.isCurrentUserMemberOf(club)) throw new ValidationException("V tomto klubu nejste členem");
        User user = userService.getCurrentUser();
        user.setRootClub(club);
        userService.update(user);
    }

    @Transactional
    public Club findUsersRootClub(User user){
        if(user.getRootClub() != null) return user.getRootClub();

        List<ClubRelation> relations = this.findAllRelationsByUser(user);
        //Is ADMIN somewhere
        Optional<ClubRelation> adminOptional = relations.stream().filter(r -> r.hasRole(UserRole.ADMIN)).findFirst();
        if(adminOptional.isPresent()) return adminOptional.get().getClub();

        //Is Professional player somewhere
        Optional<ClubRelation> playerOptional = relations.stream().filter(r -> r.hasRole(UserRole.PROFESSIONAL_PLAYER)).findFirst();
        if(playerOptional.isPresent()) return playerOptional.get().getClub();

        //Return first club
        if(!relations.isEmpty()) return relations.get(0).getClub();

        return null;
    }
}
