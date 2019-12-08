package cz.cvut.fel.tk21.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.tk21.model.Club;

public class ClubDto {

    private int id;

    private String name;

    private AddressDto address;

    private boolean isAllowedMng;

    public ClubDto() {
    }

    public ClubDto(Club club, boolean isAllowedMng) {
        this.id = club.getId();
        this.name = club.getName();
        this.address = new AddressDto(club.getAddress());
        this.isAllowedMng = isAllowedMng;
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

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    @JsonProperty(value="isAllowedMng")
    public boolean isAllowedMng() {
        return isAllowedMng;
    }

    public void setAllowedMng(boolean allowedMng) {
        isAllowedMng = allowedMng;
    }

    @JsonIgnore
    public Club getEntity(){
        Club club = new Club();
        club.setId(this.id);
        club.setName(this.name);
        club.setAddress(this.address.getEntity());
        return club;
    }

}
