package cz.cvut.fel.tk21.rest.dto.post;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Post;

public class ClubPostDto {

    private String name;

    public ClubPostDto(Club club) {
        this.name = club.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
