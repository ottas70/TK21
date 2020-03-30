package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.TournamentDao;
import cz.cvut.fel.tk21.model.tournament.Tournament;
import org.springframework.stereotype.Service;

@Service
public class TournamentService extends BaseService<TournamentDao, Tournament> {

    protected TournamentService(TournamentDao dao) {
        super(dao);
    }

}
