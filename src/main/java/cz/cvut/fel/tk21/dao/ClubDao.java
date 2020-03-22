package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Address;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.util.StringUtils;
import org.eclipse.persistence.internal.jpa.EJBQueryImpl;
import org.eclipse.persistence.jpa.JpaQuery;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
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

    public Optional<Club> findClubByWebId(int webId){
        try{
            return Optional.ofNullable(
                    em.createQuery("SELECT c from Club c " +
                            "WHERE c.webId = :webId ", Club.class)
                            .setParameter("webId", webId)
                            .getSingleResult()
            );
        } catch (NoResultException ex){
            return Optional.empty();
        }
    }

    public List<Club> findClubsByNameOrCity(String name, int page, int size){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Club> query = cb.createQuery(Club.class);
        Root<Club> root = query.from(Club.class);

        query.select(root);
        Predicate predicateName = cb.like(root.get("nameSearch"), "%" + StringUtils.stripAccentsWhitespaceAndToLowerCase(name) + "%");
        Predicate predicateCity = cb.like(root.get("address").get("city"), "%" + name + "%");
        Predicate predicateOr = cb.or(predicateCity, predicateName);

        query.where(predicateOr);
        query.orderBy(cb.desc(root.get("registered")));

        TypedQuery<Club> typedQuery = em.createQuery(query);
        typedQuery.setFirstResult((page-1) * size);
        typedQuery.setMaxResults(size);

        return typedQuery.getResultList();
    }

    public long countClubsByNameOrCity(String name){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Club> root = query.from(Club.class);

        query.select(cb.count(root));
        Predicate predicateName = cb.like(root.get("nameSearch"), "%" + StringUtils.stripAccentsWhitespaceAndToLowerCase(name) + "%");
        Predicate predicateCity = cb.like(root.get("address").get("city"), "%" + name + "%");
        Predicate predicateOr = cb.or(predicateCity, predicateName);

        query.where(predicateOr);

        return em.createQuery(query).getSingleResult();
    }

    public boolean isNameUnique(String name) {
        Optional<Club> club = findClubByName(name);
        return club.isEmpty();
    }

    public boolean isAddressUnique(Address address) {
        Optional<Club> club = findClubByAddress(address);
        return club.isEmpty();
    }

    public List<Club> findAllPaginated(int page, int size){
        return em.createQuery("SELECT c FROM Club c", Club.class)
                .setFirstResult((page-1) * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Club> findAllByContactEmail(String email){
        return em.createQuery("SELECT c from Club c " +
                "WHERE :email MEMBER OF c.emails", Club.class)
                .setParameter("email", email)
                .getResultList();
    }

}
