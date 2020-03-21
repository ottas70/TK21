package cz.cvut.fel.tk21.model;

import javax.persistence.*;

@Embeddable
public class Address {

    @Column
    private String street;

    @Column
    private String city;

    @Column
    private String zip;

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
}
