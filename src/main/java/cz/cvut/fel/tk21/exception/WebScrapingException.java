package cz.cvut.fel.tk21.exception;

public class WebScrapingException extends Exception {

    public WebScrapingException(String message) {
        super(message);
    }

    public WebScrapingException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebScrapingException(Throwable cause) {
        super(cause);
    }

}
