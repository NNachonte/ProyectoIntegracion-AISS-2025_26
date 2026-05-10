package aiss_L3.PeerTubeMiner.exception;

public class PeerTubeApiException extends RuntimeException {

    public PeerTubeApiException(String message) {
        super(message);
    }

    public PeerTubeApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
