package cz.cvut.fel.tk21.service.storage;

import com.tinify.*;
import cz.cvut.fel.tk21.exception.FileStorageException;
import cz.cvut.fel.tk21.exception.ValidationException;
import cz.cvut.fel.tk21.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;

@Service
public class ImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageService.class);
    private static final int MAX_IMAGE_SIZE = 2000;
    private static final int MIN_IMAGE_WIDTH = 2000;
    private static final int MINIATURE_IMAGE_WIDTH = 280;

    public byte[] compressImage(byte[] image) {
        try {
            byte[] resultData = Tinify.fromBuffer(image).toBuffer();
            return resultData;
        } catch (AccountException | ClientException | ServerException | ConnectionException e) {
            log.info(e.getMessage());
        } catch (java.lang.Exception e) {
            log.error(e.getMessage());
        }
        return image;
    }

    public byte[] resizeImageIfTooLarge(MultipartFile file) {
        try {
            byte[] image = file.getBytes();
            InputStream in = new ByteArrayInputStream(image);
            BufferedImage originalImage = ImageIO.read(in);

            int height = originalImage.getHeight();
            int width = originalImage.getWidth();

            if (width >= height && width > MAX_IMAGE_SIZE) {
                float ratio = MAX_IMAGE_SIZE / (float) width;
                BufferedImage buffImage = scale(originalImage, ratio);
                String type = FileUtil.getExtension(file.getContentType()).substring(1);
                return imageToByteArray(buffImage, type);
            } else if (height >= width && width > MAX_IMAGE_SIZE) {
                float ratio = MAX_IMAGE_SIZE / (float) height;
                BufferedImage buffImage = scale(originalImage, ratio);
                String type = FileUtil.getExtension(file.getContentType()).substring(1);
                return imageToByteArray(buffImage, type);
            } else {
                return image;
            }

        } catch (IOException e) {
            log.info("Failed to parse image to byte array");
            throw new FileStorageException("Something went wrong");
        }
    }

    public void checkImageSize(MultipartFile file) {
        try {
            byte[] image = file.getBytes();
            InputStream in = new ByteArrayInputStream(image);
            BufferedImage originalImage = ImageIO.read(in);

            int height = originalImage.getHeight();
            int width = originalImage.getWidth();
            float ratio = (float) width / (float) height;

            if (width < 280) throw new ValidationException("Šířka obrázku musí být alespoň 280");
            if (ratio > 10 || ratio < 0.1) throw new ValidationException("Rozměry obrázku nevyhovují požadavkům");

        } catch (IOException e) {
            throw new ValidationException("Něco se nepovedlo");
        }
    }

    //key - width, value - height
    public AbstractMap.SimpleEntry<Integer, Integer> getSize(byte[] image) {
        try {
            InputStream in = new ByteArrayInputStream(image);
            BufferedImage originalImage = ImageIO.read(in);

            int height = originalImage.getHeight();
            int width = originalImage.getWidth();

            return new AbstractMap.SimpleEntry<>(width, height);
        } catch (IOException e) {
            throw new ValidationException("Něco se nepovedlo");
        }
    }

    public byte[] createMiniature(byte[] image, String contentType) {
        try {
            InputStream in = new ByteArrayInputStream(image);
            BufferedImage originalImage = ImageIO.read(in);

            int width = originalImage.getWidth();

            float ratio = MINIATURE_IMAGE_WIDTH / (float) width;
            BufferedImage buffImage = scale(originalImage, ratio);
            String type = FileUtil.getExtension(contentType).substring(1);
            return imageToByteArray(buffImage, type);
        } catch (IOException e) {
            log.info("Failed to parse image to byte array");
            throw new FileStorageException("Something went wrong");
        }
    }

    private BufferedImage scale(BufferedImage imageToScale, float ratio) {
        BufferedImage scaledImage = null;
        if (imageToScale != null) {
            int newWidth = (int) (imageToScale.getWidth() * ratio);
            int newHeight = (int) (imageToScale.getHeight() * ratio);
            scaledImage = new BufferedImage(newWidth, newHeight, imageToScale.getType());
            Graphics2D graphics2D = scaledImage.createGraphics();
            graphics2D.drawImage(imageToScale, 0, 0, newWidth, newHeight, null);
            graphics2D.dispose();
        }
        return scaledImage;
    }

    private byte[] imageToByteArray(BufferedImage image, String type) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, type, bos);
        return bos.toByteArray();
    }

}
