package cz.cvut.fel.tk21.rest.dto;

public class CreatedDto {

    private int id;

    public CreatedDto(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CreatedDto{" +
                "id=" + id +
                '}';
    }
}
