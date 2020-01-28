package cz.cvut.fel.tk21.rest;

import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.Post;
import cz.cvut.fel.tk21.rest.dto.CreatedDto;
import cz.cvut.fel.tk21.rest.dto.post.PostDto;
import cz.cvut.fel.tk21.service.ClubService;
import cz.cvut.fel.tk21.service.PostService;
import cz.cvut.fel.tk21.util.RequestBodyValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private RequestBodyValidator validator;

    @RequestMapping(value = "/{post_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public PostDto getPost(@PathVariable("post_id") Integer post_id){
        final Optional<Post> post = postService.find(post_id);
        post.orElseThrow(() -> new NotFoundException("Příspěvek nebyl nalezen"));

        return new PostDto(post.get());
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
