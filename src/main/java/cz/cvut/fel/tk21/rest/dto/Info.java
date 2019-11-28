package cz.cvut.fel.tk21.rest.dto;

public class Info {

    private String message;

    public Info(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Info{" +
                "message='" + message + '\'' +
                '}';
    }
}
