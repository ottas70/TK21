package cz.cvut.fel.tk21.rest.dto.user;

import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.rest.dto.club.ClubRelationshipDto;

public class UserResponseDto {

    private int id;

    private String name;

    private String surname;

    private String email;

    private boolean isScraped;

    private ClubRelationshipDto pinnedClub;

    public UserResponseDto(User user, ClubRelation clubRelation) {
        this.id = user.getId();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.email = user.getEmail();
        this.isScraped = user.isScraped();
        if(clubRelation != null) this.pinnedClub = new ClubRelationshipDto(clubRelation);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ClubRelationshipDto getPinnedClub() {
        return pinnedClub;
    }

    public void setPinnedClub(ClubRelationshipDto pinnedClub) {
        this.pinnedClub = pinnedClub;
    }
}
