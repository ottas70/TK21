package cz.cvut.fel.tk21.ws.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;

public class ClubDateDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    private LocalDate date;

    private int clubId;

    @JsonIgnore
    private ObjectMapper mapper = new ObjectMapper();

    public ClubDateDto() {
    }

    public ClubDateDto(String json) {
        try {
            mapper.registerModule(new JavaTimeModule());
            ClubDateDto dateDto = mapper.readValue(json, ClubDateDto.class);
            this.date = dateDto.getDate();
            this.clubId = dateDto.getClubId();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy")
    public LocalDate getDate() {
        return date;
    }

    @JsonSetter(nulls = Nulls.SET)
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

}
