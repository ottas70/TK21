package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.ClubRelationDao;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClubRelationService extends BaseService<ClubRelationDao, ClubRelation> {

    @Autowired
    private UserService userService;

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

}
