package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.TennisCourt;
import org.springframework.stereotype.Repository;

import java.util.List;

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

}
