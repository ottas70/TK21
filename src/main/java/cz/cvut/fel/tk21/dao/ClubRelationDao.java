package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.ClubRelation;
import org.springframework.stereotype.Repository;

@Repository
public class ClubRelationDao extends BaseDao<ClubRelation> {

    protected ClubRelationDao() {
        super(ClubRelation.class);
    }

}
