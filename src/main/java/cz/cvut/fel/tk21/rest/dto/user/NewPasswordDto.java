package cz.cvut.fel.tk21.rest.dto.user;

import javax.validation.constraints.NotBlank;

public class NewPasswordDto {

    @NotBlank
    private String password;

    public NewPasswordDto() {
    }

    public NewPasswordDto(@NotBlank String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
