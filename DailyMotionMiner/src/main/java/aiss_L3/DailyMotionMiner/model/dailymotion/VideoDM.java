
package aiss_L3.DailyMotionMiner.model.dailymotion;

import javax.annotation.processing.Generated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "title",
    "description",
    "created_time",
    "channel",
    "channel.name",
    "channel.description",
    "channel.created_time",
    "owner",
    "owner.screenname",
    "owner.url",
    "owner.avatar_720_url"
})
@Generated("jsonschema2pojo")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoDM {

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("created_time")
    private Integer createdTime;

    @JsonProperty("channel")
    private String channelId;

    @JsonProperty("channel.name")
    private String channelName;

    @JsonProperty("channel.description")
    private String channelDescription;

    @JsonProperty("channel.created_time")
    private Integer channelCreatedTime;

    @JsonProperty("owner")
    private String ownerId;

    @JsonProperty("owner.screenname")
    private String ownerScreenname;

    @JsonProperty("owner.url")
    private String ownerUrl;

    @JsonProperty("owner.avatar_720_url")
    private String ownerAvatar720Url;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("created_time")
    public Integer getCreatedTime() {
        return createdTime;
    }

    @JsonProperty("created_time")
    public void setCreatedTime(Integer createdTime) {
        this.createdTime = createdTime;
    }

    @JsonProperty("channel")
    public String getChannelId() {
        return channelId;
    }

    @JsonProperty("channel")
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @JsonProperty("channel.name")
    public String getChannelName() {
        return channelName;
    }

    @JsonProperty("channel.name")
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    @JsonProperty("channel.description")
    public String getChannelDescription() {
        return channelDescription;
    }

    @JsonProperty("channel.description")
    public void setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
    }

    @JsonProperty("channel.created_time")
    public Integer getChannelCreatedTime() {
        return channelCreatedTime;
    }

    @JsonProperty("channel.created_time")
    public void setChannelCreatedTime(Integer channelCreatedTime) {
        this.channelCreatedTime = channelCreatedTime;
    }

    @JsonProperty("owner")
    public String getOwnerId() {
        return ownerId;
    }

    @JsonProperty("owner")
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @JsonProperty("owner.screenname")
    public String getOwnerScreenname() {
        return ownerScreenname;
    }

    @JsonProperty("owner.screenname")
    public void setOwnerScreenname(String ownerScreenname) {
        this.ownerScreenname = ownerScreenname;
    }

    @JsonProperty("owner.url")
    public String getOwnerUrl() {
        return ownerUrl;
    }

    @JsonProperty("owner.url")
    public void setOwnerUrl(String ownerUrl) {
        this.ownerUrl = ownerUrl;
    }

    @JsonProperty("owner.avatar_720_url")
    public String getOwnerAvatar720Url() {
        return ownerAvatar720Url;
    }

    @JsonProperty("owner.avatar_720_url")
    public void setOwnerAvatar720Url(String ownerAvatar720Url) {
        this.ownerAvatar720Url = ownerAvatar720Url;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(VideoDM.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null) ? "<null>" : this.id));
        sb.append(',');
        sb.append("title");
        sb.append('=');
        sb.append(((this.title == null) ? "<null>" : this.title));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null) ? "<null>" : this.description));
        sb.append(',');
        sb.append("createdTime");
        sb.append('=');
        sb.append(((this.createdTime == null) ? "<null>" : this.createdTime));
        sb.append(',');
        sb.append("channelId");
        sb.append('=');
        sb.append(((this.channelId == null) ? "<null>" : this.channelId));
        sb.append(',');
        sb.append("channelName");
        sb.append('=');
        sb.append(((this.channelName == null) ? "<null>" : this.channelName));
        sb.append(',');
        sb.append("channelDescription");
        sb.append('=');
        sb.append(((this.channelDescription == null) ? "<null>" : this.channelDescription));
        sb.append(',');
        sb.append("channelCreatedTime");
        sb.append('=');
        sb.append(((this.channelCreatedTime == null) ? "<null>" : this.channelCreatedTime));
        sb.append(',');
        sb.append("ownerId");
        sb.append('=');
        sb.append(((this.ownerId == null) ? "<null>" : this.ownerId));
        sb.append(',');
        sb.append("ownerScreenname");
        sb.append('=');
        sb.append(((this.ownerScreenname == null) ? "<null>" : this.ownerScreenname));
        sb.append(',');
        sb.append("ownerUrl");
        sb.append('=');
        sb.append(((this.ownerUrl == null) ? "<null>" : this.ownerUrl));
        sb.append(',');
        sb.append("ownerAvatar720Url");
        sb.append('=');
        sb.append(((this.ownerAvatar720Url == null) ? "<null>" : this.ownerAvatar720Url));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}
