package cz.cvut.fel.tk21.model;

public enum Day {

    MONDAY("Pondělí"), TUESDAY("Úterý"), WEDNESDAY("Středa"), THURSDAY("Čtvrtek"),
    FRIDAY("Pátek"), SATURDAY("Sobota"), SUNDAY("Neděle");

    private final String name;

    Day(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
