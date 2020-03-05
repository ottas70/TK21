package cz.cvut.fel.tk21.service.storage;

import cz.cvut.fel.tk21.config.properties.FileStorageProperties;
import cz.cvut.fel.tk21.exception.FileStorageException;
import cz.cvut.fel.tk21.exception.NotFoundException;
import cz.cvut.fel.tk21.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;

@Service
public class FileStorageService {

    private Path imagePath;

    @Autowired
    public FileStorageService(FileStorageProperties properties){
        this.imagePath = Paths.get(properties.getImagesPath()).toAbsolutePath().normalize();
    }

    public String storeImage(MultipartFile file){
        String fileName = generateFilename(file);

        try {
            fileName = fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            Path targetLocation = this.imagePath.resolve(fileName);
            while (Files.exists(targetLocation)){
                targetLocation = this.imagePath.resolve(generateFilename(file));
            }
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

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

    private String generateFilename(MultipartFile file){
        return generateRandomString(30) + FileUtil.getExtension(file);
    }

    private String generateRandomString(int length){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
