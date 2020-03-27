package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.*;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

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

    public List<ClubRelation> findAllRelationsByClub(Club club){
        return em.createQuery("SELECT c from ClubRelation c " +
                "WHERE c.club = :club", ClubRelation.class)
                .setParameter("club", club)
                .getResultList();
    }

    public List<ClubRelation> findAllRelationsByClubAndRole(Club club, UserRole role){
        return em.createQuery("SELECT c from ClubRelation c " +
                "WHERE c.club = :club AND :role MEMBER OF c.roles", ClubRelation.class)
                .setParameter("club", club)
                .setParameter("role", role)
                .getResultList();
    }

    public Optional<ClubRelation> findRelationByUserAndClub(User user, Club club){
        try{
            return Optional.ofNullable(em.createQuery("SELECT c FROM ClubRelation c " +
                    "WHERE c.user = :user AND c.club = :club", ClubRelation.class)
                    .setParameter("user", user)
                    .setParameter("club", club)
                    .getSingleResult());
        } catch (NoResultException e){
            return Optional.empty();
        }
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

    public boolean hasRoleSomewhere(User user, UserRole role){
        return !em.createQuery("SELECT c from ClubRelation c " +
                "WHERE c.user = :user AND :role MEMBER OF c.roles", ClubRelation.class)
                .setParameter("user", user)
                .setParameter("role", role)
                .getResultList().isEmpty();
    }

}
