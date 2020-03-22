package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.VerificationRequest;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

@Repository
public class VerificationRequestDao extends BaseDao<VerificationRequest> {

    protected VerificationRequestDao() {
        super(VerificationRequest.class);
    }

    public boolean exists(Club club, User user){
        return !em.createQuery("SELECT v from VerificationRequest v " +
                "WHERE v.user = :user AND v.club = :club", VerificationRequest.class)
                .setParameter("user", user)
                .setParameter("club", club)
                .getResultList().isEmpty();
    }

    public boolean existsUnresolvedRequest(Club club, User user){
        return !em.createQuery("SELECT v from VerificationRequest v " +
                "WHERE v.user = :user AND v.club = :club " +
                "AND v.accepted = false AND v.denied = false", VerificationRequest.class)
                .setParameter("user", user)
                .setParameter("club", club)
                .getResultList().isEmpty();
    }

    public List<VerificationRequest> findAllVerificationRequestsByClub(Club club){
        return em.createQuery("SELECT v from VerificationRequest v " +
                "WHERE v.club = :club", VerificationRequest.class)
                .setParameter("club", club)
                .getResultList();
    }

    public List<VerificationRequest> findUnresolvedVerificationRequestsByClub(Club club){
        return em.createQuery("SELECT v from VerificationRequest v " +
                "WHERE v.club = :club AND v.accepted = false AND v.denied = false", VerificationRequest.class)
                .setParameter("club", club)
                .getResultList();
    }

    public Optional<VerificationRequest> findOpenVerificationRequestByClubAndUser(Club club, User user){
        try{
            return Optional.ofNullable(
                    em.createQuery("SELECT v from VerificationRequest v " +
                            "WHERE v.club = :club AND v.user = :user " +
                            "AND v.accepted = false AND v.denied = false", VerificationRequest.class)
                            .setParameter("club", club)
                            .setParameter("user", user)
                            .getSingleResult()
            );
        } catch (NoResultException ex){
            return Optional.empty();
        }
    }

    public int countVerificationRequests(Club club){
        return findUnresolvedVerificationRequestsByClub(club).size();
    }

}
