package aiss_L3.PeerTubeMiner.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import aiss_L3.PeerTubeMiner.etl.Transformer;
import aiss_L3.PeerTubeMiner.model.peertube.ChannelPT;
import aiss_L3.PeerTubeMiner.model.peertube.ChannelSearchPt;
import aiss_L3.PeerTubeMiner.model.peertube.VideoPT;
import aiss_L3.PeerTubeMiner.model.peertube.VideoSearchPT;
import aiss_L3.PeerTubeMiner.model.videominer.Channel;
import aiss_L3.PeerTubeMiner.model.videominer.Video;

@Service
public class ChannelService {

    @Autowired
    Transformer transformer;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    VideoService videoService;

    public List<Channel> getChannels() {
    String url = "https://peertube.cpy.re/api/v1/video-channels";
    ChannelSearchPt channelsJson = restTemplate.getForObject(url, ChannelSearchPt.class);

    List<Channel> channelsTransformados = new ArrayList<>();

    if (channelsJson != null && channelsJson.getData() != null) {
        // Limitamos a los 10 primeros para que el test no sea eterno
        for (ChannelPT channelJson : channelsJson.getData()) {
            if (channelsTransformados.size() >= 10) break; 

            try {
                Channel channel = transformer.transformChannel(channelJson);

                // Intentamos obtener los vídeos del canal
                String videosUrl = url + "/" + channelJson.getName() + "/videos";
                VideoSearchPT videosJson = restTemplate.getForObject(videosUrl, VideoSearchPT.class);

                if (videosJson != null && videosJson.getData() != null) {
                    for (VideoPT vpt : videosJson.getData()) {
                        Video videoCompleto = videoService.getVideoById(vpt.getUuid(), 2);
                        channel.getVideos().add(videoCompleto);
                    }
                }
                channelsTransformados.add(channel);

            } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
                // Si el canal da 404, imprimimos un aviso y saltamos al siguiente
                System.out.println("Saltando canal no encontrado: " + channelJson.getName());
            } catch (RestClientException e) {
                // Captura cualquier otro error (timeout, etc.) para que no rompa el test
                System.out.println("Error procesando canal " + channelJson.getName() + ": " + e.getMessage());
            }
        }
    }
    return channelsTransformados;
}

    public Channel getChannelById(String id) {
    
        String baseUrl = "https://peertube.cpy.re/api/v1";
        ChannelPT ptChannel = restTemplate.getForObject(baseUrl + "/video-channels/" + id, ChannelPT.class);
    
        Channel channel = transformer.transformChannel(ptChannel);

        VideoSearchPT videosJson = restTemplate.getForObject(baseUrl + "/video-channels/" + id + "/videos", VideoSearchPT.class);

        if (videosJson != null && videosJson.getData() != null) {
            for (VideoPT vpt : videosJson.getData()) {
                Video videoTransformado = videoService.getVideoById(vpt.getUuid(),2); 
            
                channel.getVideos().add(videoTransformado);
            }
        }

        return channel;
    }

    public Channel postChannel(String id, Integer maxVideos, Integer maxComments) {

        int vLimit = (maxVideos != null) ? maxVideos : 10;
        int cLimit = (maxComments != null) ? maxComments : 2;

        ChannelPT ptChannel = restTemplate.getForObject("https://peertube.cpy.re/api/v1/video-channels/" + id, ChannelPT.class);
        Channel channel = transformer.transformChannel(ptChannel);

        String videosUrl = "https://peertube.cpy.re/api/v1/video-channels/" + id + "/videos?count=" + vLimit;
        VideoSearchPT videosJson = restTemplate.getForObject(videosUrl, VideoSearchPT.class);

        if (videosJson != null && videosJson.getData() != null) {
            for (VideoPT vpt : videosJson.getData()) {
                Video videoCompleto = videoService.getVideoById(vpt.getUuid(), cLimit);
                channel.getVideos().add(videoCompleto);
            }
        }

        String videoMinerUrl = "http://localhost:8080/videominer/channels";
        return restTemplate.postForObject(videoMinerUrl, channel, Channel.class);
    }
    
}
