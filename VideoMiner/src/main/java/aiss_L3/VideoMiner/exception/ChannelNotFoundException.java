// ChannelNotFoundException.java
package aiss_L3.VideoMiner.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Channel not found")
public class ChannelNotFoundException extends Exception {
    public ChannelNotFoundException() { super("Channel not found"); }
}