package cz.cvut.fel.tk21.scraping.service;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.UserRole;
import cz.cvut.fel.tk21.service.ClubRelationService;
import cz.cvut.fel.tk21.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlayerScrapingService {

    private final UserService userService;
    private final ClubRelationService clubRelationService;

    @Autowired
    public PlayerScrapingService(UserService userService, ClubRelationService clubRelationService) {
        this.userService = userService;
        this.clubRelationService = clubRelationService;
    }

    public void handleUpdate(User found, User stored){
        stored.setName(found.getName());
        stored.setSurname(found.getSurname());

        Club foundProfClub = found.getRootClub();
        List<ClubRelation> relations = clubRelationService.findAllRelationsWithRole(stored, UserRole.PROFESSIONAL_PLAYER);
        ClubRelation storedProfClubRelation = relations.isEmpty() ? null : relations.get(0);
        Club storedProfClub = storedProfClubRelation == null ? null : storedProfClubRelation.getClub();
        if(storedProfClubRelation != null && foundProfClub != null && !foundProfClub.equals(storedProfClub)){
            storedProfClubRelation.removeRole(UserRole.PROFESSIONAL_PLAYER);
            storedProfClubRelation.addRole(UserRole.RECREATIONAL_PLAYER);
            clubRelationService.update(storedProfClubRelation);
        }

        userService.update(stored);
    }

}
