package cz.cvut.fel.tk21.rest.dto.club.member;

import cz.cvut.fel.tk21.model.ClubRelation;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.model.UserRole;

import java.util.Collection;

public class MemberDto {

    private User user;

    private Collection<UserRole> relationship;

    public MemberDto(ClubRelation clubRelation) {
        this.user = clubRelation.getUser();
        this.relationship = clubRelation.getRoles();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection<UserRole> getRelationship() {
        return relationship;
    }

    public void setRelationship(Collection<UserRole> relationship) {
        this.relationship = relationship;
    }
}
