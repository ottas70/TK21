package cz.cvut.fel.tk21.util;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.text.Normalizer;

public class StringUtils {

    public static String stripAccentsWhitespaceAndToLowerCase(String s){
        s = s.toLowerCase().replace(" ", "");
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    public static boolean isValidEmail(String s){
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(s);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

}
