package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.BadRequestException;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Post;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.rest.dto.CreatedDto;
import cz.cvut.fel.tk21.rest.dto.club.ClubDto;
import cz.cvut.fel.tk21.rest.dto.club.ClubSearchDto;
import cz.cvut.fel.tk21.rest.dto.post.PostDto;
import cz.cvut.fel.tk21.rest.dto.post.PostsPaginatedDto;
import cz.cvut.fel.tk21.rest.dto.post.PostsWithClubPaginatedDto;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.PostService;
import cz.cvut.fel.tk21.service.UserService;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/post")
public class PostController {

    private static final String DEFAULT_SIZE_OF_PAGE = "20";

    @Autowired
    private PostService postService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestBodyValidator validator;

    @RequestMapping(value = "/{post_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PostDto getPost(@PathVariable("post_id") Integer post_id){
        final Optional<Post> post = postService.find(post_id);
        post.orElseThrow(() -> new NotFoundException("Příspěvek nebyl nalezen"));

        return new PostDto(post.get());
    }

    @RequestMapping(value = "/club/{club_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PostsPaginatedDto getAllPostsByClub(
            @PathVariable("club_id") Integer club_id,
            @RequestParam(value="page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value="size", required = false, defaultValue = DEFAULT_SIZE_OF_PAGE) Integer size){
        if(size < 1) throw new BadRequestException("Size cannot be less than zero");
        if(page < 1) page = 1;

        final Optional<Club> club = clubService.find(club_id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        return postService.findPostsPaginatedByClub(club.get(), page, size);
    }

    @RequestMapping(value = "/user/{user_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PostsWithClubPaginatedDto getAllPostsByUser(
            @PathVariable("user_id") Integer user_id,
            @RequestParam(value="page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value="size", required = false, defaultValue = DEFAULT_SIZE_OF_PAGE) Integer size){
        if(size < 1) throw new BadRequestException("Size cannot be less than zero");
        if(page < 1) page = 1;

        final Optional<User> user = userService.find(user_id);
        user.orElseThrow(() -> new NotFoundException("Uživatel nebyl nalezen"));

        return postService.findPostsPaginatedByUser(user.get(), page, size);
    }

    @RequestMapping(value = "/club/{club_id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPost(@PathVariable("club_id") Integer club_id, @RequestBody PostDto postDto){
        validator.validate(postDto);

        final Optional<Club> club = clubService.find(club_id);
        club.orElseThrow(() -> new NotFoundException("Klub nebyl nalezen"));

        Post persistedPost = postService.createPostFromDto(postDto, club.get());

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreatedDto(persistedPost.getId()));
    }

    @RequestMapping(value = "/{post_id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePost(@PathVariable("post_id") Integer post_id, @RequestBody PostDto postDto){
        validator.validate(postDto);

        final Optional<Post> post = postService.find(post_id);
        post.orElseThrow(() -> new NotFoundException("Příspěvek nebyl nalezen"));

        postService.updatePostFromDto(post.get(), postDto);

        return ResponseEntity.noContent().build();
    }

    @RequestMapping(value = "/{post_id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deletePost(@PathVariable("post_id") Integer post_id){
        final Optional<Post> post = postService.find(post_id);
        post.orElseThrow(() -> new NotFoundException("Příspěvek nebyl nalezen"));

        postService.deletePost(post.get());

        return ResponseEntity.noContent().build();
    }

}
