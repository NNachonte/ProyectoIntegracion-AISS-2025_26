package aiss_L3.TwitchMiner.services;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import aiss_L3.TwitchMiner.etl.Transformer;
import aiss_L3.TwitchMiner.exception.ResourceNotFoundException;
import aiss_L3.TwitchMiner.exception.TwitchApiException;
import aiss_L3.TwitchMiner.model.twitch.TwitchSearchChannel;
import aiss_L3.TwitchMiner.model.twitch.TwitchSearchChannelResponse;
import aiss_L3.TwitchMiner.model.twitch.TwitchUser;
import aiss_L3.TwitchMiner.model.videominer.Channel;
import aiss_L3.TwitchMiner.model.videominer.Video;

@Service
public class ChannelService {

    private static final int DEFAULT_MAX_CHANNELS = 10;
    private static final int DEFAULT_MAX_VIDEOS = 10;

    @Value("${twitch.base-url:https://api.twitch.tv/helix}")
    private String baseUrl;

    @Value("${twitch.default-query:gaming}")
    private String defaultQuery;

    @Autowired
    private Transformer transformer;

    @Autowired
    private VideoService videoService;

    @Autowired
    private TwitchClient twitchClient;

    @Autowired
    private TwitchUserService twitchUserService;

    @Autowired
    private RestTemplate restTemplate;

    public List<Channel> getChannels(String query, Integer maxResults) {
        int limit = (maxResults != null && maxResults > 0) ? maxResults : DEFAULT_MAX_CHANNELS;
        String safeQuery = StringUtils.hasText(query) ? query : defaultQuery;

        String encoded = UriUtils.encodeQueryParam(safeQuery, StandardCharsets.UTF_8);
        String url = baseUrl + "/search/channels?query=" + encoded + "&first=" + limit;

        TwitchSearchChannelResponse response;
        try {
            response = twitchClient.get(url, TwitchSearchChannelResponse.class);
        } catch (RestClientException e) {
            throw new TwitchApiException("Error fetching channels from Twitch", e);
        }

        List<Channel> channels = new ArrayList<>();
        if (response != null && response.getData() != null) {
            for (TwitchSearchChannel channelResult : response.getData()) {
                TwitchUser user = twitchUserService.getUserById(channelResult.getId());
                Channel channel = transformer.transformChannel(user);
                if (channel != null) {
                    channels.add(channel);
                }
            }
        }

        return channels;
    }

    public Channel getChannelById(String idOrLogin, Integer maxVideos) {
        TwitchUser user = twitchUserService.getUserByIdOrLogin(idOrLogin);
        if (user == null) {
            throw new ResourceNotFoundException("Channel not found on Twitch: " + idOrLogin);
        }

        Channel channel = transformer.transformChannel(user);
        int limit = (maxVideos != null && maxVideos > 0) ? maxVideos : DEFAULT_MAX_VIDEOS;

        List<Video> videos = videoService.getVideosByUserId(user.getId(), limit, user);
        if (videos != null) {
            channel.getVideos().addAll(videos);
        }

        return channel;
    }

    public Channel postChannel(String idOrLogin, Integer maxVideos) {
        Channel channel = getChannelById(idOrLogin, maxVideos);

        String videoMinerUrl = "http://localhost:8080/videominer/channels";
        try {
            System.out.println("Posting channel to VideoMiner: " + videoMinerUrl);
            System.out.println("Channel data: " + channel);
            Channel result = restTemplate.postForObject(videoMinerUrl, channel, Channel.class);
            System.out.println("Successfully posted channel to VideoMiner");
            return result;
        } catch (RestClientException e) {
            System.err.println("Failed to post channel to VideoMiner: " + e.getMessage());
            e.printStackTrace();
            throw new TwitchApiException("Error posting channel to VideoMiner: " + e.getMessage(), e);
        }
    }
}
