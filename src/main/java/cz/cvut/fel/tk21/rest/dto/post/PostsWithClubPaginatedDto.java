package cz.cvut.fel.tk21.rest.dto.post;

import cz.cvut.fel.tk21.rest.dto.PageDto;
import cz.cvut.fel.tk21.rest.dto.club.ClubDto;

import java.util.List;

public class PostsWithClubPaginatedDto {

    private PageDto page;

    private List<PostWithClubDto> posts;

    public PostsWithClubPaginatedDto(List<PostWithClubDto> posts, PageDto page) {
        this.posts = posts;
        this.page = page;
    }

    public PostsWithClubPaginatedDto(List<PostWithClubDto> posts, int currentPage, int lastPage) {
        this.posts = posts;
        this.page = new PageDto(currentPage, lastPage);
    }

    public PageDto getPage() {
        return page;
    }

    public void setPage(PageDto page) {
        this.page = page;
    }

    public List<PostWithClubDto> getPosts() {
        return posts;
    }

    public void setPosts(List<PostWithClubDto> posts) {
        this.posts = posts;
    }
}
