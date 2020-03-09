package cz.cvut.fel.tk21.rest.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.tk21.model.ImageDetail;
import cz.cvut.fel.tk21.model.Post;
import cz.cvut.fel.tk21.rest.dto.CreatedAtDto;

import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Map;

public class PostDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CreatedAtDto created_at;

    @NotBlank
    private String title;

    private Map<String, Object> description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Collection<ImageDetail> images;

    public PostDto() {
    }

    public PostDto(Post post){
        this.id = post.getId();
        this.created_at = new CreatedAtDto(post.getCreatedAt());
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.images = post.getImages();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CreatedAtDto getCreated_at() {
        return created_at;
    }

    public void setCreated_at(CreatedAtDto created_at) {
        this.created_at = created_at;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Object> getDescription() {
        return description;
    }

    public void setDescription(Map<String, Object> description) {
        this.description = description;
    }

    public Collection<ImageDetail> getImages() {
        return images;
    }

    public void setImages(Collection<ImageDetail> images) {
        this.images = images;
    }

    @JsonIgnore
    public Post getEntity(){
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);

        return post;
    }
}
