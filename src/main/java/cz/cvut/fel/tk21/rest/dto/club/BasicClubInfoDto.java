package cz.cvut.fel.tk21.rest.dto.club;

import cz.cvut.fel.tk21.model.Club;

public class BasicClubInfoDto {

    private int id;

    private String name;

    private boolean isScraped;

    private boolean isRegistered;

    private AddressDto address;

    public BasicClubInfoDto() {
    }

    public BasicClubInfoDto(Club club) {
        this.id = club.getId();
        this.name = club.getName();
        this.isScraped = club.isWebScraped();
        this.isRegistered = club.isRegistered();
        if(club.getAddress() != null) this.address = new AddressDto(club.getAddress());
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

    public boolean isScraped() {
        return isScraped;
    }

    public void setScraped(boolean scraped) {
        isScraped = scraped;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }
}
