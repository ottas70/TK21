package cz.cvut.fel.tk21.rest.dto;

import cz.cvut.fel.tk21.model.Club;

import javax.validation.constraints.NotBlank;

public class ClubDto {

    @NotBlank(message = "Name is mandatory")
    private String name;

    private AddressDto address;

    public ClubDto() {
    }

    public ClubDto(Club club) {
        this.name = club.getName();
        this.address = new AddressDto(club.getAddress());
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

    public Club getEntity(){
        Club club = new Club();
        club.setName(this.name);
        club.setAddress(this.address.getEntity());
        return club;
    }

}
