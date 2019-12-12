package cz.cvut.fel.tk21.rest.dto.club;

import cz.cvut.fel.tk21.model.Club;

import javax.validation.constraints.NotBlank;

public class ClubRegistrationDto {

    @NotBlank(message = "Název je povinný")
    private String name;

    private AddressDto address;

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
