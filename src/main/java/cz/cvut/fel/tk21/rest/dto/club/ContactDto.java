package cz.cvut.fel.tk21.rest.dto.club;

import cz.cvut.fel.tk21.model.Club;

import javax.validation.constraints.NotBlank;

public class ContactDto {

    private String telephone;

    @NotBlank(message = "Email je povinný")
    private String email;

    private String web;

    public ContactDto() {
    }

    public ContactDto(Club club) {
        this.telephone = club.getTelephone();
        this.email = club.getEmail();
        this.web = club.getWeb();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }
}
