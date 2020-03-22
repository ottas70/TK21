package cz.cvut.fel.tk21.rest.dto.club;

import cz.cvut.fel.tk21.model.Club;

import java.util.Collection;
import java.util.List;

public class ContactDto {

    private String telephone;

    private Collection<String> emails;

    private String web;

    public ContactDto() {
    }

    public ContactDto(Club club) {
        this.telephone = club.getTelephone();
        this.emails = club.getEmails();
        this.web = club.getWeb();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Collection<String> getEmails() {
        return emails;
    }

    public void setEmails(Collection<String> emails) {
        this.emails = emails;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }
}
