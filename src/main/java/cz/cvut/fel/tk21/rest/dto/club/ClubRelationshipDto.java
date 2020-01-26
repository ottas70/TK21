package cz.cvut.fel.tk21.rest.dto.club;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.UserRole;

import java.util.Collection;
import java.util.List;

public class ClubRelationshipDto {

    private int id;

    private String name;

    private Collection<UserRole> relationship;

    public ClubRelationshipDto() {
    }

    public ClubRelationshipDto(ClubRelation clubRelation) {
        this.id = clubRelation.getClub().getId();
        this.name = clubRelation.getClub().getName();
        this.relationship = clubRelation.getRoles();
    }

    public ClubRelationshipDto(Club club, Collection<UserRole> relationship) {
        this.id = club.getId();
        this.name = club.getName();
        this.relationship = relationship;
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

    public Collection<UserRole> getRelationship() {
        return relationship;
    }

    public void setRelationship(Collection<UserRole> relationship) {
        this.relationship = relationship;
    }
}
