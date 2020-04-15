package cz.cvut.fel.tk21.rest.dto.user;

import cz.cvut.fel.tk21.model.User;

public class UserDto {

    private String name;

    private String surname;

    private String email;

    private boolean isScraped;

    public UserDto(User user) {
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.isScraped = user.isScraped();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isScraped() {
        return isScraped;
    }

    public void setScraped(boolean scraped) {
        isScraped = scraped;
    }
}
