package aiss_L3.PeerTubeMiner.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss_L3.PeerTubeMiner.etl.Transformer;
import aiss_L3.PeerTubeMiner.model.peertube.VideoPT;
import aiss_L3.PeerTubeMiner.model.peertube.VideoSearchPT;
import aiss_L3.PeerTubeMiner.model.videominer.Video;


@Service
public class VideoService {

    @Autowired
    Transformer transformer;

    @Autowired
    RestTemplate restTemplate;

    // ==========================================
    // GET VIDEOS
    // ==========================================
    public List<Video> getVideos() {
        String url = "https://peertube.cpy.re/api/v1/videos";
        
        VideoSearchPT videosJson = restTemplate.getForObject(url, VideoSearchPT.class);
        
        List<Video> videosTransformados = new ArrayList<>();
        
        if (videosJson != null && videosJson.getData() != null) {
            for (VideoPT videoJson : videosJson.getData()) {
                videosTransformados.add(transformer.transformVideo(videoJson));
            }
            return videosTransformados;
        }
        
        return null;
    }

    public Video getVideoById(String id) {
        String url = "https://peertube.cpy.re/api/v1/videos/" + id;
        
        VideoPT videoJson = restTemplate.getForObject(url, VideoPT.class);
        
        if(videoJson != null){
            return transformer.transformVideo(videoJson);
        }
        return null;
    }
}
