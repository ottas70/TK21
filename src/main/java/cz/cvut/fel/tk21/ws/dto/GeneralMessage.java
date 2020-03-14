package cz.cvut.fel.tk21.ws.dto;

public class GeneralMessage {

    private String type;

    private Object body;

    public GeneralMessage() {
    }

    public GeneralMessage(String type, Object body) {
        this.type = type;
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
