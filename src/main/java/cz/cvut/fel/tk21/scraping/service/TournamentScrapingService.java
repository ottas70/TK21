package cz.cvut.fel.tk21.scraping.service;

import cz.cvut.fel.tk21.model.tournament.Tournament;
import cz.cvut.fel.tk21.service.TournamentService;
import org.springframework.stereotype.Component;

@Component
public class TournamentScrapingService {

    private final TournamentService tournamentService;

    public TournamentScrapingService(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    public void handleUpdate(Tournament found, Tournament stored){
        if(stored == null){
            //Add new tournament
            if(found != null && found.getClub() != null){
                tournamentService.persist(found);
            }
        } else {
            if(found == null){
                //Delete tournament
                tournamentService.remove(stored);
            } else {
                //Update tournament
                stored.setDate(found.getDate());
                stored.setAgeCategory(found.getAgeCategory());
                stored.setType(found.getType());
                stored.setGender(found.getGender());
                stored.setLinkInfo(found.getLinkInfo());
                stored.setLinkResults(found.getLinkResults());
                stored.setClub(found.getClub());
                stored.setPlayers(found.getPlayers());
                tournamentService.update(stored);
            }
        }
    }

}
