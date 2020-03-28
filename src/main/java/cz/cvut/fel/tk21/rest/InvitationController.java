package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.model.Invitation;
import cz.cvut.fel.tk21.rest.dto.user.NewPasswordDto;
import cz.cvut.fel.tk21.service.InvitationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/professional/password/{token}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changePasswordNonRegisteredPlayer(@PathVariable("token") String token, @RequestBody NewPasswordDto dto){
        Optional<Invitation> invitationOptional = invitationService.findByConfirmationToken(token);
        invitationOptional.orElseThrow(() -> new BadRequestException("Chybný dotaz"));
        Invitation invitation = invitationOptional.get();

        invitationService.changePasswordAndAcceptInvitation(invitation, dto.getPassword());

        return ResponseEntity.ok().build();
    }

}
