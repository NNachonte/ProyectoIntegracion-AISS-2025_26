package aiss_L3.TwitchMiner.model.twitch;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchUserResponse {

    @JsonProperty("data")
    private List<TwitchUser> data;

    public List<TwitchUser> getData() {
        return data;
    }

    public void setData(List<TwitchUser> data) {
        this.data = data;
    }
}
