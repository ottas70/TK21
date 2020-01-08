package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.TennisCourt;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

@Repository
public class CourtDao extends BaseDao<TennisCourt> {

    protected CourtDao() {
        super(TennisCourt.class);
    }

    public List<TennisCourt> findAllCourtsByClub(Club club){
        return em.createQuery("SELECT t FROM TennisCourt t " +
                "WHERE t.club = :club", TennisCourt.class)
                .setParameter("club", club)
                .getResultList();
    }

    public boolean isNameUniqueInClub(Club club, String name){
        return em.createQuery("SELECT c FROM TennisCourt c " +
                "WHERE c.club = :club AND c.name = :name", TennisCourt.class)
                .setParameter("club", club)
                .setParameter("name", name)
                .getResultList()
                .isEmpty();
    }

    public Optional<TennisCourt> findCourtByNameAndClub(Club club, String name){
        try{
            return Optional.ofNullable(em.createQuery("SELECT c FROM TennisCourt c " +
                    "WHERE c.club = :club AND c.name = :name", TennisCourt.class)
                    .setParameter("club", club)
                    .setParameter("name", name)
                    .getSingleResult());
        } catch (NoResultException e){
            return Optional.empty();
        }
    }

    public Optional<TennisCourt> findCourtByClubAndId(Club club, Integer id){
        try{
            return Optional.ofNullable(em.createQuery("SELECT c FROM TennisCourt c " +
                    "WHERE c.club = :club AND c.id = :id", TennisCourt.class)
                    .setParameter("club", club)
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException e){
            return Optional.empty();
        }
    }

}
