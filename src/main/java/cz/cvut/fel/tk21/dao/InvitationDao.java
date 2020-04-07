package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Invitation;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class InvitationDao extends BaseDao<Invitation>{

    protected InvitationDao() {
        super(Invitation.class);
    }

    public Optional<Invitation> findByConfirmationToken(String token) {
        try{
            return Optional.ofNullable(
                    em.createQuery("SELECT i from Invitation i " +
                            "WHERE i.confirmationToken = :token", Invitation.class)
                            .setParameter("token", token)
                            .getSingleResult()
            );
        } catch (NoResultException ex){
            return Optional.empty();
        }
    }

    public List<Invitation> findUnusedInvitationsAfterExpiration(){
        Date now = new Date();
        Date threeDaysAgo = new Date(now.getTime() - (3 * 86400000));
        return em.createQuery("SELECT i FROM Invitation i " +
                "WHERE i.createdAt > :expDate", Invitation.class)
                .setParameter("expDate", threeDaysAgo)
                .getResultList();
    }

}
