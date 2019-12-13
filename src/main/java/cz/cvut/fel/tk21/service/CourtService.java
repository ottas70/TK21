package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.CourtDao;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.TennisCourt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourtService extends BaseService<CourtDao, TennisCourt> {

    @Autowired
    private ClubService clubService;

    protected CourtService(CourtDao dao) {
        super(dao);
    }

    @Transactional
    public List<TennisCourt> findAllByClub(Club club){
        return dao.findAllCourtsByClub(club);
    }

}
