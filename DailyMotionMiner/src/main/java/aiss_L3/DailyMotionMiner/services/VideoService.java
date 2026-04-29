package aiss_L3.DailyMotionMiner.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss_L3.DailyMotionMiner.etl.Transformer;
import aiss_L3.DailyMotionMiner.model.dailymotion.CaptionDM;
import aiss_L3.DailyMotionMiner.model.dailymotion.CaptionSearchDM;
import aiss_L3.DailyMotionMiner.model.dailymotion.CommentDM;
import aiss_L3.DailyMotionMiner.model.dailymotion.CommentSearchDM;
import aiss_L3.DailyMotionMiner.model.dailymotion.VideoDM;
import aiss_L3.DailyMotionMiner.model.dailymotion.VideoSearchDM;
import aiss_L3.DailyMotionMiner.model.videominer.Caption;
import aiss_L3.DailyMotionMiner.model.videominer.Comment;
import aiss_L3.DailyMotionMiner.model.videominer.Video;


@Service
public class VideoService {

    @Autowired
    Transformer transformer;

    @Autowired
    RestTemplate restTemplate;

    private final String baseUrl = "https://api.dailymotion.com";

    private final String videoFields = "id,title,description,created_time,channel,channel.name,channel.description,channel.created_time,owner,owner.screenname,owner.url,owner.avatar_720_url";

    // ==========================================
    // GET VIDEOS
    // ==========================================
    public List<Video> getVideos() {
        String url = baseUrl + "/videos?fields=" + videoFields;
        VideoSearchDM videosJson = restTemplate.getForObject(url, VideoSearchDM.class);

        List<Video> videosTransformados = new ArrayList<>();

        if (videosJson != null && videosJson.getList() != null) {
            for (VideoDM videoJson : videosJson.getList()) {
                if (videoJson == null || videoJson.getId() == null) continue;
                Video videoCompleto = this.getVideoById(videoJson.getId(), 2);
                if (videoCompleto != null) {
                    videosTransformados.add(videoCompleto);
                }
            }
        }

        return videosTransformados;
    }

    public Video getVideoById(String id) {
        return getVideoById(id, 2);
    }

    public Video getVideoById(String id, Integer maxComments) {
        String videoUrl = baseUrl + "/video/" + id + "?fields=" + videoFields;
        VideoDM videoJson = restTemplate.getForObject(videoUrl, VideoDM.class);
        if (videoJson == null) return null;

        Video video = transformer.transformVideo(videoJson);

        int limit = (maxComments != null) ? maxComments : 2;

        String commentsUrl = baseUrl + "/video/" + id + "/comments?fields=id,message,created_time&limit=" + limit;
        CommentSearchDM commentResponse = restTemplate.getForObject(commentsUrl, CommentSearchDM.class);
        if (commentResponse != null && commentResponse.getList() != null) {
            for (CommentDM dmComment : commentResponse.getList()) {
                Comment comment = transformer.transformComment(dmComment);
                if (comment != null) {
                    video.getComments().add(comment);
                }
            }
        }

        String captionsUrl = baseUrl + "/video/" + id + "/subtitles?fields=id,language,language_label,url,format";
        CaptionSearchDM captionResponse = restTemplate.getForObject(captionsUrl, CaptionSearchDM.class);
        if (captionResponse != null && captionResponse.getList() != null) {
            for (CaptionDM dmCaption : captionResponse.getList()) {
                Caption caption = transformer.transformCaption(dmCaption);
                if (caption != null) {
                    video.getCaptions().add(caption);
                }
            }
        }

        return video;
    }
}
