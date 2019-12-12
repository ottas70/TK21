package cz.cvut.fel.tk21.rest.dto.club;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.tk21.model.SurfaceType;
import cz.cvut.fel.tk21.model.TennisCourt;

public class CourtDto {

    private int id;

    private String name;

    private boolean availableInSummer;

    private boolean AvailableInWinter;

    private SurfaceType surfaceType;

    public CourtDto(int id, String name, boolean availableInSummer, boolean availableInWinter, SurfaceType surfaceType) {
        this.id = id;
        this.name = name;
        this.availableInSummer = availableInSummer;
        AvailableInWinter = availableInWinter;
        this.surfaceType = surfaceType;
    }

    public CourtDto(TennisCourt court){
        this.id = court.getId();
        this.name = court.getName();
        this.availableInSummer = court.isAvailableInSummer();
        AvailableInWinter = court.isAvailableInWinter();
        this.surfaceType = court.getSurfaceType();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty(value="availableInSummer")
    public boolean isAvailableInSummer() {
        return availableInSummer;
    }

    public void setAvailableInSummer(boolean availableInSummer) {
        this.availableInSummer = availableInSummer;
    }

    @JsonProperty(value="availableInWinter")
    public boolean isAvailableInWinter() {
        return AvailableInWinter;
    }

    public void setAvailableInWinter(boolean availableInWinter) {
        AvailableInWinter = availableInWinter;
    }

    public SurfaceType getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(SurfaceType surfaceType) {
        this.surfaceType = surfaceType;
    }
}
