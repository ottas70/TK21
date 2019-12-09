package cz.cvut.fel.tk21.rest.dto;

public class PageDto {

    private int current;

    private int last;

    public PageDto(int current, int last) {
        this.current = current;
        this.last = last;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getLast() {
        return last;
    }

    public void setLast(int last) {
        this.last = last;
    }
}
