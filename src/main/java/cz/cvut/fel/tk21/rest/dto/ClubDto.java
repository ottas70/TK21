package cz.cvut.fel.tk21.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.tk21.model.Club;

public class ClubDto {

    private int id;

    private String name;

    private AddressDto address;

    public ClubDto() {
    }

    public ClubDto(Club club) {
        this.id = club.getId();
        this.name = club.getName();
        this.address = new AddressDto(club.getAddress());
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

    @JsonIgnore
    public Club getEntity(){
        Club club = new Club();
        club.setId(this.id);
        club.setName(this.name);
        club.setAddress(this.address.getEntity());
        return club;
    }

}
