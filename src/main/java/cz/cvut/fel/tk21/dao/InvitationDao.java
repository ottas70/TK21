package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Invitation;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
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

}
