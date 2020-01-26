package cz.cvut.fel.tk21.rest.dto.court;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.tk21.model.SurfaceType;
import cz.cvut.fel.tk21.model.TennisCourt;

import javax.validation.constraints.NotBlank;

public class CourtDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;

    @NotBlank
    private String name;

    private boolean availableInSummer;

    private boolean availableInWinter;

    private SurfaceType surfaceType;

    public CourtDto(int id, String name, boolean availableInSummer, boolean availableInWinter, SurfaceType surfaceType) {
        this.id = id;
        this.name = name;
        this.availableInSummer = availableInSummer;
        this.availableInWinter = availableInWinter;
        this.surfaceType = surfaceType;
    }

    public CourtDto(TennisCourt court){
        this.id = court.getId();
        this.name = court.getName();
        this.availableInSummer = court.isAvailableInSummer();
        this.availableInWinter = court.isAvailableInWinter();
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
        return availableInWinter;
    }

    public void setAvailableInWinter(boolean availableInWinter) {
        availableInWinter = availableInWinter;
    }

    public SurfaceType getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(SurfaceType surfaceType) {
        this.surfaceType = surfaceType;
    }

    @JsonIgnore
    public TennisCourt getEntity(){
        TennisCourt court = new TennisCourt();
        court.setName(this.name);
        court.setSurfaceType(this.surfaceType);
        court.setAvailableInSummer(this.availableInSummer);
        court.setAvailableInWinter(this.availableInWinter);
        return court;
    }
}
