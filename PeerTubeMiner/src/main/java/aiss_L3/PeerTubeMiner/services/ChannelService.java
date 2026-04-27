package aiss_L3.PeerTubeMiner.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss_L3.PeerTubeMiner.etl.Transformer;
import aiss_L3.PeerTubeMiner.model.peertube.ChannelPT;
import aiss_L3.PeerTubeMiner.model.peertube.ChannelSearchPt;
import aiss_L3.PeerTubeMiner.model.videominer.Channel;

@Service
public class ChannelService {

    @Autowired
    Transformer transformer;

    @Autowired
    RestTemplate restTemplate;

    public List<Channel> getChannels() {
        String url = "https://peertube.cpy.re/api/v1/channels";

        ChannelSearchPt channelsJson = restTemplate.getForObject(url, ChannelSearchPt.class);

        List<Channel> channelsTransformados = new ArrayList<>();

        if (channelsJson != null && channelsJson.getData() != null) {
            for (ChannelPT channelJson : channelsJson.getData()) {
                channelsTransformados.add(transformer.transformChannel(channelJson));
            }
        }

        return channelsTransformados;
    }

    public Channel getChannelById(String id) {
        String url = "https://peertube.cpy.re/api/v1/channels/" + id;

        ChannelPT channelJson = restTemplate.getForObject(url, ChannelPT.class);

        if (channelJson != null) {
            return transformer.transformChannel(channelJson);
        }
        return null;
    }
    
}
