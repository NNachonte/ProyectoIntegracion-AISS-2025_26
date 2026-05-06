package aiss_L3.TwitchMiner.model.twitch;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchSearchChannelResponse {

    @JsonProperty("data")
    private List<TwitchSearchChannel> data;

    public List<TwitchSearchChannel> getData() {
        return data;
    }

    public void setData(List<TwitchSearchChannel> data) {
        this.data = data;
    }
}
