package cz.cvut.fel.tk21.scraping.service;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.UserRole;
import cz.cvut.fel.tk21.service.ClubRelationService;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClubScrapingService {

    private final ClubService clubService;
    private final ClubRelationService clubRelationService;
    private final UserService userService;

    @Autowired
    public ClubScrapingService(ClubService clubService, ClubRelationService clubRelationService, UserService userService) {
        this.clubService = clubService;
        this.clubRelationService = clubRelationService;
        this.userService = userService;
    }

    public void handleUpdate(Club found, Club stored){
        if(found == null) return;

        if(stored == null){
            //Add new club
            clubService.persist(found);
        } else {
            //Update existing club
            stored.setName(found.getName());
            stored.setAddress(found.getAddress());
            stored.setEmails(found.getEmails());
            clubService.update(stored);
        }
    }

    public void handleNonFoundedClubs(List<Club> notFound){
        for (Club club : notFound){
            if(club.isRegistered()){
                List<ClubRelation> relations = clubRelationService.findAllRelationsByClubAndRole(club, UserRole.PROFESSIONAL_PLAYER);
                for (ClubRelation relation : relations){
                    relation.removeRole(UserRole.PROFESSIONAL_PLAYER);
                    relation.addRole(UserRole.RECREATIONAL_PLAYER);
                    clubRelationService.update(relation);

                    User user = relation.getUser();
                    user.setWebId(0);
                    userService.update(user);
                }

                club.setWebId(0);
                clubService.update(club);
            } else {
                clubService.remove(club);
            }
        }
    }

}
