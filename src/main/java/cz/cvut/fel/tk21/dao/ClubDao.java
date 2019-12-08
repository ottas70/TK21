package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Address;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.util.StringUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
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

    public List<Club> findClubsByName(String name){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Club> query = cb.createQuery(Club.class);
        Root<Club> root = query.from(Club.class);

        query.select(root);
        query.where(cb.like(
                cb.lower(
                        cb.function("REPLACE", String.class, root.get("name"), cb.literal(" ") , cb.literal(""))
                ),
                "%"+name.toLowerCase().replace(" ", "")+"%"));
        return em.createQuery(query).getResultList();
    }

    public boolean isNameUnique(String name) {
        Optional<Club> club = findClubByName(name);
        return club.isEmpty();
    }

    public boolean isAddressUnique(Address address) {
        Optional<Club> club = findClubByAddress(address);
        return club.isEmpty();
    }

}
