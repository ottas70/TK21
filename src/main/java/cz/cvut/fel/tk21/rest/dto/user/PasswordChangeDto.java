package cz.cvut.fel.tk21.rest.dto.user;

import javax.validation.constraints.NotBlank;

public class PasswordChangeDto {

    @NotBlank
    private String oldPass;

    @NotBlank
    private String newPass;

    public String getOldPass() {
        return oldPass;
    }

    public void setOldPass(String oldPass) {
        this.oldPass = oldPass;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }
}
