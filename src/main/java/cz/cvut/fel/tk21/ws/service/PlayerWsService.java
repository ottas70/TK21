package cz.cvut.fel.tk21.ws.service;

import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Invitation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.UserRole;
import cz.cvut.fel.tk21.scraping.scrapers.PlayerScraper;
import cz.cvut.fel.tk21.service.ClubRelationService;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.InvitationService;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.ws.dto.PlayerInfoMessageBody;
import cz.cvut.fel.tk21.ws.dto.helperDto.PlayerInfoCzTenis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PlayerWsService {

    private final ClubService clubService;
    private final ClubRelationService clubRelationService;
    private final UserService userService;
    private final InvitationService invitationService;
    private final PlayerScraper playerScraper;

    @Autowired
    public PlayerWsService(ClubService clubService, ClubRelationService clubRelationService, UserService userService, InvitationService invitationService, PlayerScraper playerScraper) {
        this.clubService = clubService;
        this.clubRelationService = clubRelationService;
        this.userService = userService;
        this.invitationService = invitationService;
        this.playerScraper = playerScraper;
    }

    public String invitePlayer(PlayerInfoCzTenis player){
        Optional<Club> club = clubService.find(player.getClubId());
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));
        Optional<User> user = userService.findUserByEmail(player.getPlayerEmail());
        if(user.isPresent()){
            return inviteRegisteredUser(player, user.get(), club.get());
        } else {
            return inviteNonRegisteredUser(player, club.get());
        }
    }

    public String inviteRegisteredUser(PlayerInfoCzTenis playerInfo, User user, Club club){
        if(clubRelationService.hasRoleSomewhere(user, UserRole.PROFESSIONAL_PLAYER)) throw new ValidationException("Uživatel již je někde závodním hráčem");

        Invitation invite = invitationService.createInvitation(club, user, playerInfo.getId());
        invitationService.persist(invite);

        invitationService.sendProfessionalPlayerInviteMail(invite);
        return "SUCCESS";
    }

    public String inviteNonRegisteredUser(PlayerInfoCzTenis player, Club club){
        //TODO
        return "NOT IMPLEMENTED YET";
    }

    public boolean isAuthorizedToRegisterPlayer(User user, Club club){
        return clubService.isUserAllowedToManageThisClub(user, club);
    }

    public boolean isAlreadyRegistered(String email, Club club){
        Optional<User> user = userService.findUserByEmail(email);
        if(user.isEmpty()) return false;
        if(clubRelationService.hasRole(club, user.get(), UserRole.PROFESSIONAL_PLAYER)){
            throw new ValidationException("Uživatel již je propojen s CzTenis");
        }
        return false;
    }

    public Optional<Club> findClub(int clubId){
        return clubService.find(clubId);
    }

    public List<PlayerInfoCzTenis> findPlayerOnCzTenis(PlayerInfoMessageBody dto, Club club) throws IOException {
        return playerScraper.findPlayersOnCzTenis(dto, club);
    }

}
