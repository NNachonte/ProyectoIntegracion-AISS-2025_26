package aiss_L3.PeerTubeMiner.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss_L3.PeerTubeMiner.etl.Transformer;
import aiss_L3.PeerTubeMiner.model.peertube.CaptionPT;
import aiss_L3.PeerTubeMiner.model.peertube.CaptionSearchPT;
import aiss_L3.PeerTubeMiner.model.peertube.CommentPT;
import aiss_L3.PeerTubeMiner.model.peertube.CommentSearchPT;
import aiss_L3.PeerTubeMiner.model.peertube.VideoPT;
import aiss_L3.PeerTubeMiner.model.peertube.VideoSearchPT;
import aiss_L3.PeerTubeMiner.model.videominer.Caption;
import aiss_L3.PeerTubeMiner.model.videominer.Comment;
import aiss_L3.PeerTubeMiner.model.videominer.Video;


@Service
public class VideoService {

    @Autowired
    Transformer transformer;

    @Autowired
    RestTemplate restTemplate;

    private final String baseUrl = "https://peertube.cpy.re/api/v1";

    public List<Video> getVideos() {
        String url = baseUrl + "/videos";
        VideoSearchPT videosJson = restTemplate.getForObject(url, VideoSearchPT.class);
    
        List<Video> videosTransformados = new ArrayList<>();
    
        if (videosJson != null && videosJson.getData() != null) {
            for (VideoPT videoJson : videosJson.getData()) {
                Video videoCompleto = this.getVideoById(videoJson.getUuid(),2);
                videosTransformados.add(videoCompleto);
            }
        }
        return videosTransformados;
    }

    public Video getVideoById(String id,Integer maxComments) {
        String videoUrl = baseUrl + "/videos/" + id;
        
        VideoPT videoJson = restTemplate.getForObject(videoUrl, VideoPT.class);
        if (videoJson == null) return null;
        
        Video video = transformer.transformVideo(videoJson);

        int limit = (maxComments != null) ? maxComments : 2;

        String commentsUrl = videoUrl + "/comment-threads?count="+limit;
        CommentSearchPT commentResponse = restTemplate.getForObject(commentsUrl, CommentSearchPT.class);
        
        if (commentResponse != null && commentResponse.getData() != null) {
            for (CommentPT ptComment : commentResponse.getData()) {
                Comment comment = transformer.transformComment(ptComment);
                video.getComments().add(comment); 
            }
        }
        
        String captionsUrl = videoUrl + "/captions";
        CaptionSearchPT captionResponse = restTemplate.getForObject(captionsUrl, CaptionSearchPT.class);
        
        if (captionResponse != null && captionResponse.getData() != null) {
            for (CaptionPT ptCaption : captionResponse.getData()) {
                Caption caption = transformer.transformCaption(ptCaption);
                video.getCaptions().add(caption); 
            }
        }

        return video;
    }
}
