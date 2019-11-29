package cz.cvut.fel.tk21.model;

public enum UserRole {
    ADMIN("ADMIN"), EMPLOYEE("EMPLOYEE"), PROFESIONAL_PLAYER("PROFESIONAL_PLAYER"),
    RECREATIONAL_PLAYER("RECREATIONAL_PLAYER");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
