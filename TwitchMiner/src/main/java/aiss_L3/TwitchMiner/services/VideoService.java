package aiss_L3.TwitchMiner.services;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriUtils;

import aiss_L3.TwitchMiner.etl.Transformer;
import aiss_L3.TwitchMiner.exception.TwitchApiException;
import aiss_L3.TwitchMiner.model.twitch.TwitchSearchChannelResponse;
import aiss_L3.TwitchMiner.model.twitch.TwitchUser;
import aiss_L3.TwitchMiner.model.twitch.TwitchVideo;
import aiss_L3.TwitchMiner.model.twitch.TwitchVideoResponse;
import aiss_L3.TwitchMiner.model.videominer.Video;

@Service
public class VideoService {

    private static final int DEFAULT_MAX_VIDEOS = 10;

    @Value("${twitch.base-url:https://api.twitch.tv/helix}")
    private String baseUrl;

    @Value("${twitch.default-query:gaming}")
    private String defaultQuery;

    @Autowired
    private Transformer transformer;

    @Autowired
    private TwitchClient twitchClient;

    @Autowired
    private TwitchUserService twitchUserService;

    public List<Video> getVideos(String query, Integer maxResults) {
        int limit = (maxResults != null && maxResults > 0) ? maxResults : DEFAULT_MAX_VIDEOS;
        String safeQuery = StringUtils.hasText(query) ? query : defaultQuery;

        String encoded = UriUtils.encodeQueryParam(safeQuery, StandardCharsets.UTF_8);
        String searchUrl = baseUrl + "/search/channels?query=" + encoded + "&first=1";

        TwitchSearchChannelResponse searchResponse;
        try {
            searchResponse = twitchClient.get(searchUrl, TwitchSearchChannelResponse.class);
        } catch (RestClientException e) {
            throw new TwitchApiException("Error searching channels for videos", e);
        }

        if (searchResponse == null || searchResponse.getData() == null || searchResponse.getData().isEmpty()) {
            return new ArrayList<>();
        }

        String userId = searchResponse.getData().get(0).getId();
        TwitchUser user = twitchUserService.getUserById(userId);
        return getVideosByUserId(userId, limit, user);
    }

    public Video getVideoById(String id) {
        if (!StringUtils.hasText(id)) return null;

        String encoded = UriUtils.encodeQueryParam(id, StandardCharsets.UTF_8);
        String url = baseUrl + "/videos?id=" + encoded;

        TwitchVideoResponse response;
        try {
            response = twitchClient.get(url, TwitchVideoResponse.class);
        } catch (RestClientException e) {
            throw new TwitchApiException("Error fetching video from Twitch", e);
        }

        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            return null;
        }

        TwitchVideo twitchVideo = response.getData().get(0);
        TwitchUser user = twitchUserService.getUserById(twitchVideo.getUserId());
        return transformer.transformVideo(twitchVideo, user);
    }

    public List<Video> getVideosByUserId(String userId, Integer maxVideos, TwitchUser user) {
        int limit = (maxVideos != null && maxVideos > 0) ? maxVideos : DEFAULT_MAX_VIDEOS;
        if (!StringUtils.hasText(userId)) return new ArrayList<>();

        String encoded = UriUtils.encodeQueryParam(userId, StandardCharsets.UTF_8);
        String url = baseUrl + "/videos?user_id=" + encoded + "&first=" + limit;

        TwitchVideoResponse response;
        try {
            response = twitchClient.get(url, TwitchVideoResponse.class);
        } catch (RestClientException e) {
            throw new TwitchApiException("Error fetching videos for user", e);
        }

        List<Video> videos = new ArrayList<>();
        if (response != null && response.getData() != null) {
            for (TwitchVideo twitchVideo : response.getData()) {
                Video video = transformer.transformVideo(twitchVideo, user);
                if (video != null) {
                    videos.add(video);
                }
            }
        }

        return videos;
    }
}
