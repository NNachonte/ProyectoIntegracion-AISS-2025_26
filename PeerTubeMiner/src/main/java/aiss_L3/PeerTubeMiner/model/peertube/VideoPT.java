
package aiss_L3.PeerTubeMiner.model.peertube;

import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "uuid",
    "name",
    "description",
    "publishedAt",
    "account",
    "channel"
})
@Generated("jsonschema2pojo")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoPT {

    @JsonProperty("uuid")
    private String uuid;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("publishedAt")
    private String publishedAt;
    @JsonProperty("account")
    private AccountPT account;
    @JsonProperty("channel")
    private ChannelPT channel;

    @JsonProperty("uuid")
    public String getUuid() {
        return uuid;
    }

    @JsonProperty("uuid")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("publishedAt")
    public String getPublishedAt() {
        return publishedAt;
    }

    @JsonProperty("publishedAt")
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    @JsonProperty("account")
    public AccountPT getAccount() {
        return account;
    }

    @JsonProperty("account")
    public void setAccount(AccountPT account) {
        this.account = account;
    }

    @JsonProperty("channel")
    public ChannelPT getChannel() {
        return channel;
    }

    @JsonProperty("channel")
    public void setChannel(ChannelPT channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(VideoPT.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("uuid");
        sb.append('=');
        sb.append(((this.uuid == null)?"<null>":this.uuid));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null)?"<null>":this.description));
        sb.append(',');
        sb.append("publishedAt");
        sb.append('=');
        sb.append(((this.publishedAt == null)?"<null>":this.publishedAt));
        sb.append(',');
        sb.append("account");
        sb.append('=');
        sb.append(((this.account == null)?"<null>":this.account));
        sb.append(',');
        sb.append("channel");
        sb.append('=');
        sb.append(((this.channel == null)?"<null>":this.channel));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}
