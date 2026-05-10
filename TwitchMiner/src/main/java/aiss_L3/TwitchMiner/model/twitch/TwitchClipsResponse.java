package aiss_L3.TwitchMiner.model.twitch;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wrapper for Twitch Clips API response.
 * Maps to /helix/clips endpoint response structure.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchClipsResponse {

    @JsonProperty("data")
    private List<TwitchClip> data;

    @JsonProperty("pagination")
    private Object pagination;

    public List<TwitchClip> getData() {
        return data;
    }

    public void setData(List<TwitchClip> data) {
        this.data = data;
    }

    public Object getPagination() {
        return pagination;
    }

    public void setPagination(Object pagination) {
        this.pagination = pagination;
    }
}
