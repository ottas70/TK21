package cz.cvut.fel.tk21.model;

import cz.cvut.fel.tk21.exception.BadRequestException;

public enum UserRole {
    ADMIN("ADMIN"), EMPLOYEE("EMPLOYEE"), PROFESSIONAL_PLAYER("PROFESSIONAL_PLAYER"),
    RECREATIONAL_PLAYER("RECREATIONAL_PLAYER");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static UserRole getRoleFromString(String name) {
        switch (name){
            case "ADMIN": return ADMIN;
            case "EMPLOYEE": return EMPLOYEE;
            case "PROFESSIONAL_PLAYER": return PROFESSIONAL_PLAYER;
            case "RECREATIONAL_PLAYER": return RECREATIONAL_PLAYER;
            default: return null;
        }
    }
}
