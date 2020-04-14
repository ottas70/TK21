package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.annotation.ClubManagementOnly;
import cz.cvut.fel.tk21.dao.VerificationRequestDao;
import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.*;
import cz.cvut.fel.tk21.model.mail.Mail;
import cz.cvut.fel.tk21.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VerificationRequestService extends BaseService<VerificationRequestDao, VerificationRequest>{

    @Autowired
    private UserService userService;

    @Autowired
    private ClubRelationService clubRelationService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private MailService mailService;

    protected VerificationRequestService(VerificationRequestDao dao) {
        super(dao);
    }

    @Transactional
    public void addVerificationRequestToClub(Club club){
        User user = userService.getCurrentUser();
        if(user == null) throw new UnauthorizedException("Přístup zamítnut");

        if(club.isUserBlocked(user)) throw new ValidationException("BLOCKED");
        if(clubRelationService.isMemberOf(club, user)) throw new ValidationException("ALREADY A MEMBER");
        if(dao.existsUnresolvedRequest(club, user)) throw new ValidationException("REQUEST EXISTS");

        VerificationRequest verificationRequest = new VerificationRequest();
        verificationRequest.setClub(club);
        verificationRequest.setUser(user);
        verificationRequest.setCreatedAt(new Date());
        verificationRequest.setAccepted(false);
        verificationRequest.setDenied(false);

        this.persist(verificationRequest);

        this.sendVerificationRequestSummaryEmail(user.getEmail(), club, verificationRequest);

        for (User admin : clubRelationService.findAllUsersWithRelation(club, UserRole.ADMIN)){
            this.sendVerificationRequestSummaryAdminEmail(admin.getEmail(), club, verificationRequest);
        }
    }

    @Transactional
    @ClubManagementOnly
    public List<VerificationRequest> findUnresolvedVerificationRequestsByClub(Club club){
        return dao.findUnresolvedVerificationRequestsByClub(club);
    }

    @Transactional
    public void deleteAllVerificationRequestsByClub(Club club){
        dao.findUnresolvedVerificationRequestsByClub(club).forEach(this::remove);
    }

    @Transactional
    @ClubManagementOnly
    public boolean processVerification(Club club, User user, String message){
        Optional<VerificationRequest> verificationRequest = dao.findOpenVerificationRequestByClubAndUser(club, user);
        verificationRequest.orElseThrow(() -> new ValidationException("Požadavek nebyl nalezen"));

        if(message.equals("ACCEPT")){
            acceptVerificationRequest(verificationRequest.get());
            return true;
        }else if(message.equals("DENIED")){
            denyVerificationRequest(verificationRequest.get());
            return false;
        } else{
            throw new BadRequestException("Incorrect message sent");
        }
    }

    @Transactional
    public void acceptVerificationRequest(VerificationRequest verificationRequest){
        verificationRequest.setDenied(false);
        verificationRequest.setAccepted(true);

        this.update(verificationRequest);
        clubRelationService.addUserToClub(verificationRequest.getClub(), verificationRequest.getUser(), UserRole.RECREATIONAL_PLAYER);
        this.sendVerificationRequestAcceptedEmail(verificationRequest);
    }

    @Transactional
    public void denyVerificationRequest(VerificationRequest verificationRequest){
        verificationRequest.setAccepted(false);
        verificationRequest.setDenied(true);

        this.update(verificationRequest);
        this.sendVerificationRequestDeniedEmail(verificationRequest);
    }

    @Transactional
    public int getNumOfVerificationRequests(Club club){
        if(!clubService.isCurrentUserAllowedToManageThisClub(club)) return -1;
        return dao.countVerificationRequests(club);
    }

    @Transactional
    public boolean hasUserUnresolvedRequest(Club club, User user){
        return dao.existsUnresolvedRequest(club, user);
    }

    private void sendVerificationRequestSummaryEmail(String email, Club club, VerificationRequest request){
        Mail mail = new Mail();
        mail.setFrom("noreply@tk21.cz");
        mail.setTo(email);
        mail.setSubject("Rekapitulace žádosti o členství");

        LocalDateTime localDateTime = request.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDate localDate = localDateTime.toLocalDate();
        LocalTime localTime = localDateTime.toLocalTime();

        Map<String, Object> model = new HashMap<>();
        model.put("clubID", club.getId());
        model.put("clubName", club.getName());
        model.put("day", String.format("%02d", localDate.getDayOfMonth()));
        model.put("month", String.format("%02d", localDate.getMonthValue()));
        model.put("reservationYear", localDate.getYear());
        model.put("time", localTime.format(DateTimeFormatter.ofPattern("HH:mm")));

        mail.setModel(model);

        mailService.sendJoinRequestSummary(mail);
    }

    private void sendVerificationRequestSummaryAdminEmail(String email, Club club, VerificationRequest request){
        Mail mail = new Mail();
        mail.setFrom("noreply@tk21.cz");
        mail.setTo(email);
        mail.setSubject("Rekapitulace žádosti o členství");

        LocalDateTime localDateTime = request.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDate localDate = localDateTime.toLocalDate();
        LocalTime localTime = localDateTime.toLocalTime();

        Map<String, Object> model = new HashMap<>();
        model.put("clubID", club.getId());
        model.put("clubName", club.getName());
        model.put("day", String.format("%02d", localDate.getDayOfMonth()));
        model.put("month", String.format("%02d", localDate.getMonthValue()));
        model.put("reservationYear", localDate.getYear());
        model.put("time", localTime.format(DateTimeFormatter.ofPattern("HH:mm")));

        mail.setModel(model);

        mailService.sendJoinRequestSummaryAdmin(mail);
    }

    private void sendVerificationRequestAcceptedEmail(VerificationRequest request){
        Club club = request.getClub();

        Mail mail = new Mail();
        mail.setFrom("noreply@tk21.cz");
        mail.setTo(request.getUser().getEmail());
        mail.setSubject("Rozhodnutí žádosti o členství");

        Map<String, Object> model = new HashMap<>();
        model.put("clubID", club.getId());
        model.put("clubName", club.getName());
        model.put("clubEmail", club.getEmails().isEmpty() ? "Email nenalezen" : club.getEmails().stream().findFirst().get());

        mail.setModel(model);

        mailService.sendJoinRequestAccepted(mail);
    }

    private void sendVerificationRequestDeniedEmail(VerificationRequest request){
        Club club = request.getClub();

        Mail mail = new Mail();
        mail.setFrom("noreply@tk21.cz");
        mail.setTo(request.getUser().getEmail());
        mail.setSubject("Rozhodnutí žádosti o členství");

        Map<String, Object> model = new HashMap<>();
        model.put("clubID", club.getId());
        model.put("clubName", club.getName());

        mail.setModel(model);

        mailService.sendJoinRequestDenied(mail);
    }
}
