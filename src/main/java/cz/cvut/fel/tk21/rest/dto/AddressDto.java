package cz.cvut.fel.tk21.rest.dto;

import cz.cvut.fel.tk21.model.Address;

import javax.validation.constraints.NotBlank;

public class AddressDto {

    @NotBlank(message = "Street is mandatory")
    private String street;

    @NotBlank(message = "City is mandatory")
    private String city;

    @NotBlank(message = "ZIP is mandatory")
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

    public Address getEntity(){
        Address address = new Address();
        address.setStreet(this.street);
        address.setCity(this.city);
        address.setZip(this.zip);
        return address;
    }

}
