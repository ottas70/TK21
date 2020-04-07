package cz.cvut.fel.tk21.ws.service;

import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.exception.WebScrapingException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Invitation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.UserRole;
import cz.cvut.fel.tk21.scraping.scrapers.PlayerScraper;
import cz.cvut.fel.tk21.service.ClubRelationService;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.InvitationService;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.util.StringUtils;
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
        if(user.getProfessionalPlayerInvitation() != null
                && user.getProfessionalPlayerInvitation().getClub().getId() == club.getId())
            throw new ValidationException("Hráč je již pozván do vašeho klubu");

        Invitation invite = invitationService.createInvitation(club, user, playerInfo.getId());

        invitationService.sendProfessionalPlayerInviteMail(invite);
        return "SUCCESS-REGISTERED_USER";
    }

    public String inviteNonRegisteredUser(PlayerInfoCzTenis player, Club club){
        User user = userService.createNonVerifiedUser(player.getName(), player.getSurname(), player.getPlayerEmail());
        Invitation invite = invitationService.createInvitation(club, user, player.getId());
        invitationService.sendProfessionalPlayerInviteMailNonRegisteredPlayer(invite);
        return "SUCCESS-NONREGISTERED_USER";
    }

    public boolean isAuthorizedToRegisterPlayer(User user, Club club){
        return clubService.isUserAllowedToManageThisClub(user, club);
    }

    public void checkInfoValidity(PlayerInfoMessageBody info, Club club){
        if(!StringUtils.isValidEmail(info.getPlayerEmail())) throw new ValidationException("Nevalidní email");
        Optional<User> user = userService.findUserByEmail(info.getPlayerEmail());
        if(user.isEmpty()) return;
        if(!user.get().getName().toLowerCase().equals(info.getName().toLowerCase()) || !user.get().getSurname().toLowerCase().equals(info.getSurname().toLowerCase())){
            throw new ValidationException("Tento email je již registován na jiného uživatele");
        }
        if(clubRelationService.hasRole(club, user.get(), UserRole.PROFESSIONAL_PLAYER)){
            throw new ValidationException("Uživatel již je propojen s CzTenis");
        }
    }

    public Optional<Club> findClub(int clubId){
        return clubService.find(clubId);
    }

    public List<PlayerInfoCzTenis> findPlayerOnCzTenis(PlayerInfoMessageBody dto, Club club) throws IOException, WebScrapingException {
        return playerScraper.findPlayersOnCzTenis(dto, club);
    }

}
