package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.dao.PostDao;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Post;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.rest.dto.post.PostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class PostService extends BaseService<PostDao, Post> {

    @Autowired
    private ClubService clubService;

    @Autowired
    private UserService userService;

    protected PostService(PostDao dao) {
        super(dao);
    }

    @Transactional
    public Post createPostFromDto(PostDto dto, Club club){
        if(!clubService.isCurrentUserAllowedToManageThisClub(club)) throw  new UnauthorizedException("Přístup odepřen");

        User user = userService.getCurrentUser();

        Post post = dto.getEntity();
        post.setClub(club);
        post.setUser(user);
        post.setCreatedAt(new Date());

        return this.persist(post);
    }

    @Transactional
    public void updatePostFromDto(Post post, PostDto dto){
        if(!clubService.isCurrentUserAllowedToManageThisClub(post.getClub())) throw  new UnauthorizedException("Přístup odepřen");

        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());

        this.update(post);
    }

    @Transactional
    public void deletePost(Post post){
        if(!clubService.isCurrentUserAllowedToManageThisClub(post.getClub())) throw  new UnauthorizedException("Přístup odepřen");
        this.remove(post);
    }

}
