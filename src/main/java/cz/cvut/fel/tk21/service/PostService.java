package cz.cvut.fel.tk21.service;

import cz.cvut.fel.tk21.config.properties.FileStorageProperties;
import cz.cvut.fel.tk21.dao.PostDao;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.exception.UnauthorizedException;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.model.ImageDetail;
import cz.cvut.fel.tk21.model.Post;
import cz.cvut.fel.tk21.model.User;
import cz.cvut.fel.tk21.rest.dto.club.ClubDto;
import cz.cvut.fel.tk21.rest.dto.club.ClubSearchDto;
import cz.cvut.fel.tk21.rest.dto.post.*;
import cz.cvut.fel.tk21.service.storage.FileStorageService;
import cz.cvut.fel.tk21.service.storage.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService extends BaseService<PostDao, Post> {

    @Autowired
    private ClubService clubService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ImageService imageService;

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

        for (ImageDetail detail : post.getImages()){
            fileStorageService.deleteFile(detail.getOriginalName());
            fileStorageService.deleteFile(detail.getMiniName());
        }

        this.remove(post);
    }

    @Transactional
    public void deleteAllPostsByClub(Club club){
        this.findAllPostsByClub(club).forEach(this::deletePost);
    }

    @Transactional(readOnly = true)
    public List<Post> findAllPostsByClub(Club club){
        return dao.findPostsByClub(club);
    }

    @Transactional(readOnly = true)
    public PostsPaginatedDto findPostsPaginatedByClub(Club club, int page, int size) {
        List<PostDto> posts = dao.findPostsByClub(club, page, size)
                .stream().map(PostDto::new).collect(Collectors.toList());
        int lastPage = (int) Math.ceil(dao.countPostsByClub(club) / (double)size);
        return new PostsPaginatedDto(posts, page, lastPage);
    }

    @Transactional(readOnly = true)
    public PostsWithClubPaginatedDto findPostsPaginatedByUser(User user, int page, int size) {
        List<PostWithClubDto> posts = dao.findPostsByUser(user, page, size)
                .stream().map(PostWithClubDto::new).collect(Collectors.toList());
        int lastPage = (int) Math.ceil(dao.countPostsByUser(user) / (double)size);
        return new PostsWithClubPaginatedDto(posts, page, lastPage);
    }

    @Transactional(readOnly = true)
    public PostsWithClubPaginatedDto findPostsPaginatedForUser(User user, int page, int size){
        List<PostWithClubDto> posts = dao.findPostsForUser(user, page, size)
                .stream().map(PostWithClubDto::new).collect(Collectors.toList());
        int lastPage = (int) Math.ceil(dao.countPostsForUser(user) / (double)size);
        return new PostsWithClubPaginatedDto(posts, page, lastPage);
    }

    @Transactional
    public List<ImageDetail> uploadPostImages(Post post, MultipartFile[] files){
        if(!clubService.isCurrentUserAllowedToManageThisClub(post.getClub())) throw  new UnauthorizedException("Přístup odepřen");
        List<ImageDetail> details = new ArrayList<>();
        for (MultipartFile file : files){
            imageService.checkImageSize(file);
            byte[] image = imageService.resizeImageIfTooLarge(file);
            AbstractMap.SimpleEntry<Integer, Integer> sizeOriginal = imageService.getSize(image);
            String filenameOriginal = fileStorageService.storeImage(image, file.getContentType(), true);

            //create mini
            byte[] mini = imageService.createMiniature(image, file.getContentType());
            AbstractMap.SimpleEntry<Integer, Integer> sizeMini = imageService.getSize(mini);
            String filenameMini = fileStorageService.storeImage(mini, file.getContentType(), false);

            //create DB object
            ImageDetail detail = new ImageDetail();
            detail.setOriginalName(filenameOriginal);
            detail.setWidthOriginal(sizeOriginal.getKey());
            detail.setHeightOriginal(sizeOriginal.getValue());
            detail.setMiniName(filenameMini);
            detail.setWidthMini(sizeMini.getKey());
            detail.setHeightMini(sizeMini.getValue());

            post.addImage(detail);
            details.add(detail);
        }

        this.update(post);
        return details;
    }

    @Transactional
    public void deletePostImage(Post post, String filename){
        if(!clubService.isCurrentUserAllowedToManageThisClub(post.getClub())) throw  new UnauthorizedException("Přístup odepřen");
        ImageDetail detail = post.findByFilename(filename);
        if(detail == null) throw new NotFoundException("Obrázek nebyl nalezen");

        fileStorageService.deleteFile(detail.getOriginalName());
        fileStorageService.deleteFile(detail.getMiniName());
        post.removeImage(detail);
        this.update(post);
    }

}
