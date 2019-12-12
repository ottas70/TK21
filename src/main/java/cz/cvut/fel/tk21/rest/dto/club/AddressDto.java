package cz.cvut.fel.tk21.rest.dto.club;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.cvut.fel.tk21.model.Address;

import javax.validation.constraints.NotBlank;

public class AddressDto {

    @NotBlank(message = "Ulice je povinná")
    private String street;

    @NotBlank(message = "Město je povinné")
    private String city;

    @NotBlank(message = "PSČ je povinné")
    private String zip;

    public AddressDto() {
    }

    public AddressDto(Address address) {
        this.city = address.getCity();
        this.street = address.getStreet();
        this.zip = address.getZip();
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    @JsonIgnore
    public Address getEntity(){
        Address address = new Address();
        address.setStreet(this.street);
        address.setCity(this.city);
        address.setZip(this.zip);
        return address;
    }

}
