package aiss_L3.DailyMotionMiner.exception;

public class DailyMotionApiException extends RuntimeException {

    public DailyMotionApiException(String message) {
        super(message);
    }

    public DailyMotionApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
