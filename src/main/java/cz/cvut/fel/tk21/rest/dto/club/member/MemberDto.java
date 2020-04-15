package cz.cvut.fel.tk21.rest.dto.club.member;

import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.UserRole;
import cz.cvut.fel.tk21.rest.dto.user.UserDto;

import java.util.Collection;

public class MemberDto {

    private UserDto user;

    private Collection<UserRole> relationship;

    public MemberDto(ClubRelation clubRelation) {
        this.user = new UserDto(clubRelation.getUser());
        this.relationship = clubRelation.getRoles();
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public Collection<UserRole> getRelationship() {
        return relationship;
    }

    public void setRelationship(Collection<UserRole> relationship) {
        this.relationship = relationship;
    }
}
