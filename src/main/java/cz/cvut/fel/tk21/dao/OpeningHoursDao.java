package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.OpeningHours;
import org.springframework.stereotype.Repository;

@Repository
public class OpeningHoursDao extends BaseDao<OpeningHours> {

    protected OpeningHoursDao() {
        super(OpeningHours.class);
    }

}
