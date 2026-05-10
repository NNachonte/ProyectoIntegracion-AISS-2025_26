package aiss_L3.TwitchMiner.model.twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Twitch Clip fetched from the Helix API.
 * Maps to /helix/clips endpoint response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitchClip {

    @JsonProperty("id")
    private String id;

    @JsonProperty("url")
    private String url;

    @JsonProperty("embed_url")
    private String embedUrl;

    @JsonProperty("broadcaster_id")
    private String broadcasterId;

    @JsonProperty("broadcaster_name")
    private String broadcasterName;

    @JsonProperty("creator_id")
    private String creatorId;

    @JsonProperty("creator_name")
    private String creatorName;

    @JsonProperty("video_id")
    private String videoId;

    @JsonProperty("game_id")
    private String gameId;

    @JsonProperty("language")
    private String language;

    @JsonProperty("title")
    private String title;

    @JsonProperty("view_count")
    private Integer viewCount;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    @JsonProperty("duration")
    private Double duration;

    @JsonProperty("vod_offset")
    private Integer vodOffset;

    @JsonProperty("is_featured")
    private Boolean isFeatured;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getBroadcasterId() {
        return broadcasterId;
    }

    public void setBroadcasterId(String broadcasterId) {
        this.broadcasterId = broadcasterId;
    }

    public String getBroadcasterName() {
        return broadcasterName;
    }

    public void setBroadcasterName(String broadcasterName) {
        this.broadcasterName = broadcasterName;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public String getEmbedUrl() {
        return embedUrl;
    }

    public void setEmbedUrl(String embedUrl) {
        this.embedUrl = embedUrl;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Integer getVodOffset() {
        return vodOffset;
    }

    public void setVodOffset(Integer vodOffset) {
        this.vodOffset = vodOffset;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
}
