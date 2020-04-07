package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.TournamentDao;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.tournament.Tournament;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TournamentService extends BaseService<TournamentDao, Tournament> {

    protected TournamentService(TournamentDao dao) {
        super(dao);
    }

    public List<Tournament> findAllTournamentsByClub(Club club){
        return dao.findTournamentsByClub(club);
    }

    @Transactional(readOnly = true)
    public List<Tournament> findAllUpcomingTournamentsForUser(User user){
        return dao.findAllUpcomingTournamentsForUser(user);
    }

    @Transactional(readOnly = true)
    public Optional<Tournament> findTournamentByWebId(long webId){
        return dao.findTournamentByWebId(webId);
    }

}
