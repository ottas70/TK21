package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.UserRole;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClubRelationDao extends BaseDao<ClubRelation> {

    protected ClubRelationDao() {
        super(ClubRelation.class);
    }

    public List<ClubRelation> findAllRelationsByUser(User user){
        return em.createQuery("SELECT c from ClubRelation c " +
                "WHERE c.user = :user", ClubRelation.class)
                .setParameter("user", user)
                .getResultList();
    }

    public boolean hasRole(User user, Club club, UserRole role){
        return !em.createQuery("SELECT c from ClubRelation c " +
                "WHERE c.user = :user AND c.club = :club AND :role MEMBER OF c.roles", ClubRelation.class)
                .setParameter("user", user)
                .setParameter("club", club)
                .setParameter("role", role)
                .getResultList().isEmpty();
    }

    public boolean hasRelationToThisClub(User user, Club club){
        return !em.createQuery("SELECT c from ClubRelation c " +
                "WHERE c.user = :user AND c.club = :club", ClubRelation.class)
                .setParameter("user", user)
                .setParameter("club", club)
                .getResultList().isEmpty();
    }

}
