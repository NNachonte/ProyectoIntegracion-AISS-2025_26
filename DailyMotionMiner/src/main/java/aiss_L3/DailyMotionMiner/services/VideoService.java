package aiss_L3.DailyMotionMiner.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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

    private <T> T getForObjectWithRetry(String url, Class<T> responseType) {
        int attempts = 0;
        long backoffMs = 500L;

        while (true) {
            try {
                return restTemplate.getForObject(url, responseType);
            } catch (HttpClientErrorException.TooManyRequests e) {
                attempts++;
                if (attempts > 2) {
                    throw e;
                }

                long sleepMs;
                if (attempts == 1) {
                    sleepMs = resolveRetryAfterMs(e.getResponseHeaders(), backoffMs);
                } else {
                    sleepMs = backoffMs;
                }

                backoffMs = Math.min(backoffMs * 2, 2000L);
                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
    }

    private long resolveRetryAfterMs(HttpHeaders headers, long defaultMs) {
        if (headers == null) {
            return defaultMs;
        }

        String retryAfter = headers.getFirst("Retry-After");
        if (retryAfter == null) {
            return defaultMs;
        }

        try {
            long seconds = Long.parseLong(retryAfter.trim());
            if (seconds < 0) {
                return defaultMs;
            }
            return seconds * 1000L;
        } catch (NumberFormatException ex) {
            return defaultMs;
        }
    }

    // ==========================================
    // GET VIDEOS
    // ==========================================
    public List<Video> getVideos() {
        String url = baseUrl + "/videos?fields=" + videoFields;
        VideoSearchDM videosJson = getForObjectWithRetry(url, VideoSearchDM.class);

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
        VideoDM videoJson = getForObjectWithRetry(videoUrl, VideoDM.class);
        if (videoJson == null) return null;

        Video video = transformer.transformVideo(videoJson);

        int limit = (maxComments != null) ? maxComments : 2;

        String commentsUrl = baseUrl + "/video/" + id + "/comments?fields=id,message,created_time&limit=" + limit;
        CommentSearchDM commentResponse = getForObjectWithRetry(commentsUrl, CommentSearchDM.class);
        if (commentResponse != null && commentResponse.getList() != null) {
            for (CommentDM dmComment : commentResponse.getList()) {
                Comment comment = transformer.transformComment(dmComment);
                if (comment != null) {
                    video.getComments().add(comment);
                }
            }
        }

        String captionsUrl = baseUrl + "/video/" + id + "/subtitles?fields=id,language,language_label,url";
        CaptionSearchDM captionResponse = getForObjectWithRetry(captionsUrl, CaptionSearchDM.class);
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
