package cz.cvut.fel.tk21.model;

import cz.cvut.fel.tk21.exception.BadRequestException;

public enum Day {

    MONDAY("Pondělí",1), TUESDAY("Úterý", 2), WEDNESDAY("Středa", 3), THURSDAY("Čtvrtek", 4),
    FRIDAY("Pátek", 5), SATURDAY("Sobota", 6), SUNDAY("Neděle", 7);

    private final String name;
    private final int code;

    Day(String name, int code) {
        this.name = name;
        this.code = code;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getCode(){
        return code;
    }

    public static Day getDayFromCode(int code) {
        switch (code){
            case 1: return MONDAY;
            case 2: return TUESDAY;
            case 3: return WEDNESDAY;
            case 4: return THURSDAY;
            case 5: return FRIDAY;
            case 6: return SATURDAY;
            case 7: return SUNDAY;
            default: throw new BadRequestException("Invalid day code");
        }
    }

}
