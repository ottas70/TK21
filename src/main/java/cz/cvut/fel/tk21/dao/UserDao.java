package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.Optional;

@Repository
public class UserDao extends BaseDao<User>{

    public UserDao() {
        super(User.class);
    }

    public Optional<User> getUserByEmail(String email) {
        try{
            return Optional.ofNullable(
                    em.createNamedQuery("Users.getByEmail", User.class)
                            .setParameter("email", email)
                            .getSingleResult()
            );
        } catch (NoResultException ex){
            return Optional.empty();
        }
    }

    public boolean isEmailUnique(String email) {
        Optional<User> user = getUserByEmail(email);
        if(user.isPresent()){
            return false;
        }
        return true;
    }

    public Optional<User> getUserByWebId(long webId) {
        try{
            return Optional.ofNullable(
                    em.createQuery("SELECT u From User u " +
                            "WHERE u.webId = :webId", User.class)
                            .setParameter("webId", webId)
                            .getSingleResult()
            );
        } catch (NoResultException ex){
            return Optional.empty();
        }
    }

}
