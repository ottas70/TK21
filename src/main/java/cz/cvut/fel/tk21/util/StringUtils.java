package cz.cvut.fel.tk21.util;

import java.text.Normalizer;

public class StringUtils {

    public static String stripAccentsWhitespaceAndToLowerCase(String s){
        s = s.toLowerCase().replace(" ", "");
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

}
