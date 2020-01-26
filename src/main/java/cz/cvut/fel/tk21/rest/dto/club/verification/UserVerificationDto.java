package cz.cvut.fel.tk21.rest.dto.club.verification;

import javax.validation.constraints.NotBlank;

public class UserVerificationDto {

    private int userId;

    @NotBlank
    private String verification;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }
}
