package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.MatchDao;
import cz.cvut.fel.tk21.model.teams.Match;
import org.springframework.stereotype.Service;

@Service
public class MatchService extends BaseService<MatchDao, Match> {

    protected MatchService(MatchDao dao) {
        super(dao);
    }

}
