package cz.cvut.fel.tk21.dao;

import cz.cvut.fel.tk21.model.tournament.Tournament;
import org.springframework.stereotype.Repository;

@Repository
public class TournamentDao extends BaseDao<Tournament>{

    protected TournamentDao() {
        super(Tournament.class);
    }

}
