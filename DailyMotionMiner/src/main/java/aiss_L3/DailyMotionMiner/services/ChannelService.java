package aiss_L3.DailyMotionMiner.services;

import aiss_L3.DailyMotionMiner.etl.Transformer;
import aiss_L3.DailyMotionMiner.exception.DailyMotionApiException;
import aiss_L3.DailyMotionMiner.exception.ResourceNotFoundException;
import aiss_L3.DailyMotionMiner.model.dailymotion.ChannelDM;
import aiss_L3.DailyMotionMiner.model.dailymotion.ChannelSearchDM;
import aiss_L3.DailyMotionMiner.model.dailymotion.VideoDM;
import aiss_L3.DailyMotionMiner.model.dailymotion.VideoSearchDM;
import aiss_L3.DailyMotionMiner.model.videominer.Channel;
import aiss_L3.DailyMotionMiner.model.videominer.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChannelService {

    private static final Logger log = LoggerFactory.getLogger(ChannelService.class);

    @Autowired
    private Transformer transformer;

    @Autowired
    private VideoService videoService;

    @Autowired
    private RestTemplate restTemplate;

    private final String baseUrl = "https://api.dailymotion.com";
    private final String channelFields = "id,name,description,created_time";
    private final String videoFields = "id,title,description,created_time,channel,channel.name,channel.description,channel.created_time,owner,owner.screenname,owner.url,owner.avatar_720_url";

    private static final int DEFAULT_MAX_VIDEOS = 10;
    private static final int DEFAULT_MAX_PAGES = 2;
    private static final int DEFAULT_MAX_COMMENTS = 2;

    public List<Channel> getChannels() {
        String url = baseUrl + "/channels?fields=" + channelFields;
        ChannelSearchDM channelJson;

        try {
            channelJson = restTemplate.getForObject(url, ChannelSearchDM.class);
        } catch (RestClientException e) {
            throw new DailyMotionApiException("Error fetching channels from DailyMotion API", e);
        }

        List<Channel> transformedChannels = new ArrayList<>();

        if (channelJson != null && channelJson.getList() != null) {
            for (ChannelDM dmChannel : channelJson.getList()) {
                if (dmChannel == null || dmChannel.getId() == null) continue;
                transformedChannels.add(transformer.transformChannel(dmChannel));
            }
        }

        return transformedChannels;
    }

    public Channel getChannelById(String id, Integer maxVideos, Integer maxPages) {
        String channelUrl = baseUrl + "/channel/" + id + "?fields=" + channelFields;
        ChannelDM channelJson;

        try {
            channelJson = restTemplate.getForObject(channelUrl, ChannelDM.class);
        } catch (HttpClientErrorException e) {
            throw new ResourceNotFoundException("Channel not found on DailyMotion: " + id);
        } catch (HttpServerErrorException e) {
            throw new DailyMotionApiException("DailyMotion API is experiencing issues", e);
        } catch (RestClientException e) {
            throw new DailyMotionApiException("Unexpected error communicating with DailyMotion", e);
        }

        if (channelJson == null) return null;

        Channel channel = transformer.transformChannel(channelJson);

        int limitPerPage = (maxVideos != null && maxVideos > 0) ? maxVideos : DEFAULT_MAX_VIDEOS;
        int pagesToFetch = (maxPages != null && maxPages > 0) ? maxPages : DEFAULT_MAX_PAGES;

        Set<String> seenVideoIds = new HashSet<>();

        for (int page = 1; page <= pagesToFetch; page++) {
            String videosUrl = baseUrl + "/channel/" + id + "/videos?fields=" + videoFields
                    + "&limit=" + limitPerPage
                    + "&page=" + page;

            try {
                VideoSearchDM videoResponse = restTemplate.getForObject(videosUrl, VideoSearchDM.class);
                if (videoResponse == null || videoResponse.getList() == null || videoResponse.getList().isEmpty()) {
                    break;
                }

                for (VideoDM dmVideo : videoResponse.getList()) {
                    if (dmVideo == null || dmVideo.getId() == null) continue;
                    if (!seenVideoIds.add(dmVideo.getId())) continue;

                    try {
                        Video fullVideo = videoService.getVideoById(dmVideo.getId(), DEFAULT_MAX_COMMENTS);
                        if (fullVideo != null) {
                            channel.getVideos().add(fullVideo);
                        }
                    } catch (RuntimeException e) {
                        log.warn("Skipping video {} for channel {} due to error: {}", dmVideo.getId(), id, e.getMessage());
                    }
                }

                if (Boolean.FALSE.equals(videoResponse.getHasMore())) {
                    break;
                }
            } catch (RestClientException e) {
                log.warn("Error fetching videos for channel {} (page {}). Detail: {}", id, page, e.getMessage());
                break;
            }
        }

        return channel;
    }

    public Channel postChannel(String id, Integer maxVideos, Integer maxPages) {
        Channel channel = getChannelById(id, maxVideos, maxPages);

        String videoMinerUrl = "http://localhost:8080/videominer/channels";
        return restTemplate.postForObject(videoMinerUrl, channel, Channel.class);
    }
}
