package cz.cvut.fel.tk21.rest.dto.club;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.tk21.model.Club;
import cz.cvut.fel.tk21.rest.dto.court.CourtDto;

import java.util.List;
import java.util.stream.Collectors;

public class ClubDto {

    private int id;

    private String name;

    private AddressDto address;

    private List<CourtDto> courts;

    private boolean isAllowedMng;

    private boolean isAllowedRes;

    private boolean isMember;

    private int numOfRequests;

    public ClubDto() {
    }

    public ClubDto(Club club, boolean isAllowedMng, boolean isAllowedRes, boolean isMember, int numOfRequests) {
        this.id = club.getId();
        this.name = club.getName();
        this.address = new AddressDto(club.getAddress());
        this.courts = club.getCourts().stream().map(CourtDto::new).collect(Collectors.toList());
        this.isAllowedMng = isAllowedMng;
        this.isAllowedRes = isAllowedRes;
        this.isMember = isMember;
        this.numOfRequests = numOfRequests;
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

    public List<CourtDto> getCourts() {
        return courts;
    }

    public void setCourts(List<CourtDto> courts) {
        this.courts = courts;
    }

    @JsonProperty(value="isAllowedMng")
    public boolean isAllowedMng() {
        return isAllowedMng;
    }

    public void setAllowedMng(boolean allowedMng) {
        isAllowedMng = allowedMng;
    }

    @JsonProperty(value="isAllowedRes")
    public boolean isAllowedRes() {
        return isAllowedRes;
    }

    public void setAllowedRes(boolean allowedRes) {
        isAllowedRes = allowedRes;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
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
