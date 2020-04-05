package cz.cvut.fel.tk21.rest.dto.post;

import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Post;

public class ClubPostDto {

    private int id;

    private String name;

    public ClubPostDto(Club club) {
        this.id = club.getId();
        this.name = club.getName();
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
}
