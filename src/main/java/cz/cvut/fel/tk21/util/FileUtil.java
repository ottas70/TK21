package cz.cvut.fel.tk21.util;

import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

    public static boolean isImage(MultipartFile file){
        String contentType = file.getContentType();
        return contentType.equals("image/jpeg")
                || contentType.equals("image/png")
                || contentType.equals("image/gif");
    }

    public static boolean isPngOrJpg(MultipartFile file){
        String contentType = file.getContentType();
        return contentType.equals("image/jpeg")
                || contentType.equals("image/png");
    }

    public static String getExtension(MultipartFile file){
        try {
            MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
            MimeType mimeType = allTypes.forName(file.getContentType());
            return mimeType.getExtension();
        } catch (MimeTypeException e) {
            return "";
        }
    }

}
