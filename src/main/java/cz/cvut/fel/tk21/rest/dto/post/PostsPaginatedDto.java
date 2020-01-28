package cz.cvut.fel.tk21.rest.dto.post;

import cz.cvut.fel.tk21.rest.dto.PageDto;
import cz.cvut.fel.tk21.rest.dto.club.ClubDto;

import java.util.List;

public class PostsPaginatedDto {

    private PageDto page;

    private List<PostDto> posts;

    public PostsPaginatedDto(List<PostDto> posts, PageDto page) {
        this.posts = posts;
        this.page = page;
    }

    public PostsPaginatedDto(List<PostDto> posts, int currentPage, int lastPage) {
        this.posts = posts;
        this.page = new PageDto(currentPage, lastPage);
    }

    public PageDto getPage() {
        return page;
    }

    public void setPage(PageDto page) {
        this.page = page;
    }

    public List<PostDto> getPosts() {
        return posts;
    }

    public void setPosts(List<PostDto> posts) {
        this.posts = posts;
    }
}
