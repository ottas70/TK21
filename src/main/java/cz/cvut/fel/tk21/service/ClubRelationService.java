package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.ClubRelationDao;
import cz.cvut.fel.tk21.model.ClubRelation;
import org.springframework.stereotype.Service;

@Service
public class ClubRelationService extends BaseService<ClubRelationDao, ClubRelation> {

    protected ClubRelationService(ClubRelationDao dao) {
        super(dao);
    }

}
