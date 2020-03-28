package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.InvitationDao;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Invitation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.UserRole;
import cz.cvut.fel.tk21.model.mail.Mail;
import cz.cvut.fel.tk21.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class InvitationService extends BaseService<InvitationDao, Invitation>{

    private final MailService mailService;
    private final ClubRelationService clubRelationService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    protected InvitationService(InvitationDao dao, MailService mailService, ClubRelationService clubRelationService, UserService userService, PasswordEncoder passwordEncoder) {
        super(dao);
        this.mailService = mailService;
        this.clubRelationService = clubRelationService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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

        this.persist(invite);
        return invite;
    }

    @Transactional
    public void acceptInvitation(Invitation invitation){
        User user = invitation.getUser();
        Club club = invitation.getClub();

        if(!user.isVerifiedAccount()) throw new ValidationException("Uživatel není ověřen");

        if(clubRelationService.isMemberOf(club, user)){
            clubRelationService.deleteRoleWithoutPermissionCheck(club, user, UserRole.RECREATIONAL_PLAYER);
            clubRelationService.addRoleWithoutPermissionCheck(club, user, UserRole.PROFESSIONAL_PLAYER);
            //TODO uncomment
            //sendInvitationAcceptedMail(invitation);
        } else {
            clubRelationService.addUserToClub(club, user, UserRole.PROFESSIONAL_PLAYER);
        }

        user.setWebId(invitation.getWebId());
        userService.update(user);
        this.remove(invitation);
    }

    @Transactional
    public void changePasswordAndAcceptInvitation(Invitation invitation, String password){
        User user = invitation.getUser();
        Club club = invitation.getClub();

        if(user.isVerifiedAccount()) throw new ValidationException("Uživatel je již ověřen");

        user.setPassword(passwordEncoder.encode(password));
        user.setVerifiedAccount(true);
        user.setWebId(invitation.getWebId());
        userService.update(user);
        this.remove(invitation);

        clubRelationService.addUserToClub(club, user, UserRole.PROFESSIONAL_PLAYER);

        //TODO uncomment
        //sendInvitationAcceptedMail(invitation);
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

    public void sendProfessionalPlayerInviteMailNonRegisteredPlayer(Invitation invitation){
        Mail mail = new Mail();
        mail.setFrom("noreply@tk21.cz");
        mail.setTo(invitation.getUser().getEmail());
        mail.setSubject("Pozvání do aplikace TK21");

        Map<String, Object> model = new HashMap<>();
        model.put("token", invitation.getConfirmationToken());
        mail.setModel(model);

        mailService.sendProfessionalPlayerInviteNonRegisteredPlayer(mail);
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
