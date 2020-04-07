package cz.cvut.fel.tk21.scheduled;

import cz.cvut.fel.tk21.model.Invitation;
import cz.cvut.fel.tk21.service.InvitationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EverydayRunner {

    private final InvitationService invitationService;

    public EverydayRunner(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void run(){
        deleteUnusedInvitations();
    }

    private void deleteUnusedInvitations(){
        List<Invitation> toBeDeleted = invitationService.findUnusedInvitationsAfterExpiration();
        for (Invitation invitation : toBeDeleted){
            invitationService.remove(invitation);
        }
    }

}
