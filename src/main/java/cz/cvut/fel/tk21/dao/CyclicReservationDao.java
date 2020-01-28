package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.CyclicReservation;
import org.springframework.stereotype.Repository;

@Repository
public class CyclicReservationDao extends BaseDao<CyclicReservation> {

    protected CyclicReservationDao() {
        super(CyclicReservation.class);
    }

}
