package cz.cvut.fel.tk21.rest.handler;

public class VerifiedErrorInfo {

    private int status;
    private final boolean verifiedError = true;
    private String message;

    public VerifiedErrorInfo(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isVerifiedError() {
        return verifiedError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
