package cz.cvut.fel.tk21.service.storage;

import cz.cvut.fel.tk21.config.properties.FileStorageProperties;
import cz.cvut.fel.tk21.exception.FileStorageException;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.util.FileUtil;
import cz.cvut.fel.tk21.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private Path imagePath;

    @Autowired
    public FileStorageService(FileStorageProperties properties){
        this.imagePath = Paths.get(properties.getImagesPath()).toAbsolutePath().normalize();
    }

    public String storeImage(byte[] image, String contentType, boolean compress){
        //TODO uncomment for production
        if (compress && FileUtil.isPngOrJpg(contentType)) {
            //image = imageService.compressImage(image);
        }

        String fileName = generateFilename(contentType);
        Path targetLocation = this.imagePath.resolve(fileName);

        while (Files.exists(targetLocation)){
            targetLocation = this.imagePath.resolve(generateFilename(contentType));
        }

        try {
            Files.copy(new ByteArrayInputStream(image), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.imagePath.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new NotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new NotFoundException("File not found " + fileName, ex);
        }
    }

    public void deleteFile(String fileName){
        try {
            Path filePath = this.imagePath.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(!resource.exists()) throw new NotFoundException("File not found " + fileName);
            Files.delete(filePath);
        } catch (MalformedURLException ex) {
            throw new NotFoundException("File not found " + fileName, ex);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + fileName + ". Please try again!", ex);
        }
    }

    private String generateFilename(String contentType){
        return StringUtils.generateRandomString(30) + FileUtil.getExtension(contentType);
    }

}
