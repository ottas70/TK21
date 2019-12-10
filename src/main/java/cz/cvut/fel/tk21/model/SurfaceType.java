package cz.cvut.fel.tk21.model;

public enum SurfaceType {

    CLAY("ANTUKA"), HARD("TVRDÝ POVRCH"), GRASS("TRÁVA");

    private final String name;

    SurfaceType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
