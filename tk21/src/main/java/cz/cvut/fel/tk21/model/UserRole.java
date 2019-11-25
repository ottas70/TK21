package cz.cvut.fel.tk21.model;

public enum UserRole {
    ADMIN("ADMIN"), EMPLOYEE("EMPLOYEE"), PLAYER("PLAYER");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
