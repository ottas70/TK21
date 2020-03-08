package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.VerificationRequestDao;
import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.UserRole;
import cz.cvut.fel.tk21.model.VerificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VerificationRequestService extends BaseService<VerificationRequestDao, VerificationRequest>{

    @Autowired
    private UserService userService;

    @Autowired
    private ClubRelationService clubRelationService;

    @Autowired
    private ClubService clubService;

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
    }

    @Transactional
    public List<VerificationRequest> findUnresolvedVerificationRequestsByClub(Club club){
        if(!clubService.isCurrentUserAllowedToManageThisClub(club)) throw new UnauthorizedException("Přístup odepřen");
        return dao.findUnresolvedVerificationRequestsByClub(club);
    }

    @Transactional
    public boolean processVerification(Club club, User user, String message){
        if(!clubService.isCurrentUserAllowedToManageThisClub(club)) throw new UnauthorizedException("Přístup odepřen");

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
    }

    @Transactional
    public void denyVerificationRequest(VerificationRequest verificationRequest){
        verificationRequest.setAccepted(false);
        verificationRequest.setDenied(true);

        this.update(verificationRequest);
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

}
