package cz.cvut.fel.tk21.util;

import java.time.LocalDate;

public class DateUtils {

    public static int getCurrentYear(){
        return LocalDate.now().getYear();
    }

}
