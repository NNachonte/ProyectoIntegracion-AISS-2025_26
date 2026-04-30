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
import java.util.List;

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

    public Channel getChannelById(String id, Integer maxVideos, Integer maxComments) {
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

        int limit = (maxVideos != null) ? maxVideos : 10;
        String videosUrl = baseUrl + "/channel/" + id + "/videos?fields=" + videoFields + "&limit=" + limit;
        
        try {
            VideoSearchDM videoResponse = restTemplate.getForObject(videosUrl, VideoSearchDM.class);
            if (videoResponse != null && videoResponse.getList() != null) {
                for (VideoDM dmVideo : videoResponse.getList()) {
                    if (dmVideo != null && dmVideo.getId() != null) {
                        try {
                            Video fullVideo = videoService.getVideoById(dmVideo.getId(), maxComments);
                            if (fullVideo != null) {
                                channel.getVideos().add(fullVideo);
                            }
                        } catch (RuntimeException e) {
                            log.warn("Skipping video {} for channel {} due to error: {}", dmVideo.getId(), id, e.getMessage());
                        }
                    }
                }
            }
        } catch (RestClientException e) {
            log.warn("Error fetching videos for channel {}. Detail: {}", id, e.getMessage());
        }

        return channel;
    }

    public Channel postChannel(String id, Integer maxVideos, Integer maxComments) {
        Channel channel = getChannelById(id, maxVideos, maxComments);

        String videoMinerUrl = "http://localhost:8080/videominer/channels";
        return restTemplate.postForObject(videoMinerUrl, channel, Channel.class);
    }
}
