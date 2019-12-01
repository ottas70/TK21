package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Address;
import cz.cvut.fel.tk21.model.Club;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.Optional;

@Repository
public class ClubDao extends BaseDao<Club>{

    protected ClubDao() {
        super(Club.class);
    }

    public Optional<Club> findClubByName(String name){
        try{
            return Optional.ofNullable(
                    em.createQuery("SELECT c from Club c " +
                            "WHERE c.name = :name", Club.class)
                            .setParameter("name", name)
                            .getSingleResult()
            );
        } catch (NoResultException ex){
            return Optional.empty();
        }
    }

    public Optional<Club> findClubByAddress(Address address){
        try{
            return Optional.ofNullable(
                    em.createQuery("SELECT c from Club c " +
                            "WHERE c.address.street = :street AND " +
                            "c.address.city = :city AND " +
                            "c.address.zip = :zip", Club.class)
                            .setParameter("street", address.getStreet())
                            .setParameter("city", address.getCity())
                            .setParameter("zip", address.getZip())
                            .getSingleResult()
            );
        } catch (NoResultException ex){
            return Optional.empty();
        }
    }

    public boolean isNameUnique(String name) {
        Optional<Club> club = findClubByName(name);
        if(club.isPresent()){
            return false;
        }
        return true;
    }

    public boolean isAddressUnique(Address address) {
        Optional<Club> club = findClubByAddress(address);
        if(club.isPresent()){
            return false;
        }
        return true;
    }

}
