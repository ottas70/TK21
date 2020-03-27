package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.model.Invitation;
import cz.cvut.fel.tk21.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "api/invitation")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @RequestMapping(value = "/professional/{token}", method = RequestMethod.POST)
    public ResponseEntity<?> acceptInvitationAsProfessionalPlayer(@PathVariable("token") String token){
        Optional<Invitation> invitation = invitationService.findByConfirmationToken(token);
        invitation.orElseThrow(() -> new BadRequestException("Chybný dotaz"));

        invitationService.acceptInvitation(invitation.get());

        return ResponseEntity.ok().build();
    }

}
