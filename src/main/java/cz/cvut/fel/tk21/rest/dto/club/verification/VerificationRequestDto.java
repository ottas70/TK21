package cz.cvut.fel.tk21.rest.dto.club.verification;

import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.VerificationRequest;
import cz.cvut.fel.tk21.rest.dto.CreatedAtDto;

import java.util.Date;

public class VerificationRequestDto {

    private User user;

    private CreatedAtDto created_at;

    public VerificationRequestDto() {
    }

    public VerificationRequestDto(User user, Date date) {
        this.user = user;
        this.created_at = new CreatedAtDto(date);
    }

    public VerificationRequestDto(VerificationRequest verificationRequest) {
        this.user = verificationRequest.getUser();
        this.created_at = new CreatedAtDto(verificationRequest.getCreatedAt());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CreatedAtDto getCreated_at() {
        return created_at;
    }

    public void setCreated_at(CreatedAtDto created_at) {
        this.created_at = created_at;
    }
}
