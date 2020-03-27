package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.InvitationDao;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Invitation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.UserRole;
import cz.cvut.fel.tk21.model.mail.Mail;
import cz.cvut.fel.tk21.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class InvitationService extends BaseService<InvitationDao, Invitation>{

    private final MailService mailService;
    private final ClubRelationService clubRelationService;
    private final UserService userService;

    @Autowired
    protected InvitationService(InvitationDao dao, MailService mailService, ClubRelationService clubRelationService, UserService userService) {
        super(dao);
        this.mailService = mailService;
        this.clubRelationService = clubRelationService;
        this.userService = userService;
    }

    public Optional<Invitation> findByConfirmationToken(String token){
        return dao.findByConfirmationToken(token);
    }

    public Invitation createInvitation(Club club, User user, long webId){
        Invitation invite = new Invitation();
        invite.setUser(user);
        invite.setClub(club);
        invite.setCreatedAt(new Date());
        invite.setWebId(webId);
        String token = UUID.randomUUID().toString();
        while(this.findByConfirmationToken(token).isPresent()){
            token = UUID.randomUUID().toString();
        }
        invite.setConfirmationToken(token);
        return invite;
    }

    @Transactional
    public void acceptInvitation(Invitation invitation){
        User user = invitation.getUser();
        Club club = invitation.getClub();

        if(clubRelationService.isMemberOf(club, user)){
            clubRelationService.deleteRoleWithoutPermissionCheck(club, user, UserRole.RECREATIONAL_PLAYER);
            clubRelationService.addRoleWithoutPermissionCheck(club, user, UserRole.PROFESSIONAL_PLAYER);
            //TODO uncomment
            //sendInvitationAcceptedMail(invitation);
        } else {
            //TODO implement
        }

        user.setWebId(invitation.getWebId());
        userService.update(user);
        this.remove(invitation);
    }

    public void sendProfessionalPlayerInviteMail(Invitation invitation){
        Mail mail = new Mail();
        mail.setFrom("noreply@tk21.cz");
        mail.setTo(invitation.getUser().getEmail());
        mail.setSubject("Potvrzení jako závodní hráč");

        Map<String, Object> model = new HashMap<>();
        model.put("token", invitation.getConfirmationToken());
        mail.setModel(model);

        mailService.sendProfessionalPlayerInvite(mail);
    }

    public void sendInvitationAcceptedMail(Invitation invitation){
        for(String email : invitation.getClub().getEmails()){
            Mail mail = new Mail();
            mail.setFrom("noreply@tk21.cz");
            mail.setTo(email);
            mail.setSubject("Hráč potvrdil účast ve vašem klubu");

            Map<String, Object> model = new HashMap<>();
            model.put("name", invitation.getUser().getName() + " " + invitation.getUser().getSurname());
            mail.setModel(model);

            mailService.sendInvitationAccepted(mail);
        }
    }

}
