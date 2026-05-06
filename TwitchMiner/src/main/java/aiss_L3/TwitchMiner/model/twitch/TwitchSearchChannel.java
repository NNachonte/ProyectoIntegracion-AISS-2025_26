package aiss_L3.TwitchMiner.model.twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchSearchChannel {

    @JsonProperty("id")
    private String id;

    @JsonProperty("broadcaster_login")
    private String broadcasterLogin;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("title")
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBroadcasterLogin() {
        return broadcasterLogin;
    }

    public void setBroadcasterLogin(String broadcasterLogin) {
        this.broadcasterLogin = broadcasterLogin;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
