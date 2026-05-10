package aiss_L3.TwitchMiner.exception;

public class TwitchApiException extends RuntimeException {

    public TwitchApiException(String message) {
        super(message);
    }

    public TwitchApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
