package cz.cvut.fel.tk21.util;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.text.Normalizer;
import java.util.Random;

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

    public static String generateRandomString(int length){
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static boolean isTeamLetter(String letter){
        return letter.equals("A") || letter.equals("B") || letter.equals("C") || letter.equals("D")
                || letter.equals("E") || letter.equals("F") || letter.equals("G") || letter.equals("H");
    }

    public static boolean isValidPassword(String password){
        return password.length() > 6;
    }

}
