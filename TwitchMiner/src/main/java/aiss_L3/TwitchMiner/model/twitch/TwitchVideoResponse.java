package aiss_L3.TwitchMiner.model.twitch;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchVideoResponse {

    @JsonProperty("data")
    private List<TwitchVideo> data;

    public List<TwitchVideo> getData() {
        return data;
    }

    public void setData(List<TwitchVideo> data) {
        this.data = data;
    }
}
