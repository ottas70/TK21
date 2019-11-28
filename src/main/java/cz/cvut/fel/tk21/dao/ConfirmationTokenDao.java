package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.mail.ConfirmationToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ConfirmationTokenDao extends BaseDao<ConfirmationToken> {

    protected ConfirmationTokenDao() {
        super(ConfirmationToken.class);
    }

    public Optional<ConfirmationToken> findByConfirmationToken(String confirmationToken) {
        return Optional.ofNullable(
                em.createQuery("SELECT c from ConfirmationToken c " +
                        "WHERE c.confirmationToken = :token", ConfirmationToken.class)
                        .setParameter("token", confirmationToken)
                        .getSingleResult()
        );
    }

}
