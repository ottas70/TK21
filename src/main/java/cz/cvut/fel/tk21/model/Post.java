package cz.cvut.fel.tk21.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
public class Post extends AbstractEntity{

    @ManyToOne
    private User user;

    @ManyToOne
    private Club club;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column
    private String title;

    @Lob
    private String description;

    @ElementCollection
    private Collection<String> images;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<String> getImages() {
        return images;
    }

    public void setImages(Collection<String> images) {
        this.images = images;
    }

    public void addImage(String path){
        if(!images.contains(path)){
            images.add(path);
        }
    }

    public void removeImage(String path){
        images.remove(path);
    }
}
