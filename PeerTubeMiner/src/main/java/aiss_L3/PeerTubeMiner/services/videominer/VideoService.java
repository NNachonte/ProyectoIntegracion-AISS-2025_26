package aiss_L3.PeerTubeMiner.services.videominer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss_L3.PeerTubeMiner.model.peertube.VideoPT;
import aiss_L3.PeerTubeMiner.model.peertube.VideosPT;
import aiss_L3.PeerTubeMiner.model.videominer.Video;

// Importamos tus modelos (asegúrate de que los nombres coinciden con tus archivos)

@Service
public class VideoService {

    @Autowired
    RestTemplate restTemplate;

    // ==========================================
    // GET VIDEOS
    // ==========================================
    public List<Video> getVideos() {
        String url = "https://peertube.cpy.re/api/v1/videos";
        
        VideosPT videosJson = restTemplate.getForObject(url, VideosPT.class);
        
        List<Video> videosTransformados = new ArrayList<>();
        
        if (videosJson != null && videosJson.getData() != null) {
            for (VideoPT videoJson : videosJson.getData()) {
                Video v = new Video();
                
                v.setId(videoJson.getUuid());            
                v.setName(videoJson.getName());          
                v.setDescription(videoJson.getDescription()); 
                v.setReleaseTime(videoJson.getPublishedAt()); 
                
                videosTransformados.add(v);
            }
        }
        
        return videosTransformados;
    }

    // ==============================================
    // GET VIDEO BY ID
    // ==============================================
    public Video getVideoById(String id) {
        String url = "https://peertube.cpy.re/api/v1/videos/" + id;
        
        // 1. Descargamos un solo vídeo con la clase de PeerTube
        VideoPT videoJson = restTemplate.getForObject(url, VideoPT.class);
        
        if (videoJson != null) {
            Video v = new Video();
            
            v.setId(videoJson.getUuid());
            v.setName(videoJson.getName());
            v.setDescription(videoJson.getDescription());
            v.setReleaseTime(videoJson.getPublishedAt());
            
            return v;
        }
        return null;
    }
}
