package aiss_L3.PeerTubeMiner.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
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

    private final String baseUrl = "https://peertube.tv/api/v1";

    private <T> T getForObjectWithRetry(String url, Class<T> responseType) {
        int attempts = 0;
        long backoffMs = 250L;

        while (true) {
            try {
                return restTemplate.getForObject(url, responseType);
            } catch (HttpClientErrorException.TooManyRequests e) {
                attempts++;
                if (attempts >= 3) {
                    throw e;
                }
                try {
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
                backoffMs *= 2;
            }
        }
    }

    public List<Video> getVideos() {
        // Importante: NO enriquecemos cada vídeo con llamadas adicionales (comentarios/captions)
        // porque dispara rate-limits (429) en peertube.tv.
        String url = baseUrl + "/videos?count=10";

        VideoSearchPT videosJson;
        try {
            videosJson = getForObjectWithRetry(url, VideoSearchPT.class);
        } catch (RestClientException e) {
            return new ArrayList<>();
        }

        List<Video> videosTransformados = new ArrayList<>();

        if (videosJson != null && videosJson.getData() != null) {
            for (VideoPT videoJson : videosJson.getData()) {
                Video video = transformer.transformVideo(videoJson);
                if (video != null) {
                    videosTransformados.add(video);
                }
            }
        }

        return videosTransformados;
    }

    public Video getVideoById(String id,Integer maxComments) {
        String videoUrl = baseUrl + "/videos/" + id;

        VideoPT videoJson;
        try {
            videoJson = getForObjectWithRetry(videoUrl, VideoPT.class);
        } catch (RestClientException e) {
            return null;
        }
        if (videoJson == null) return null;
        
        Video video = transformer.transformVideo(videoJson);

        int limit = (maxComments != null) ? maxComments : 2;

        String commentsUrl = videoUrl + "/comment-threads?count=" + limit;
        CommentSearchPT commentResponse = null;
        try {
            commentResponse = getForObjectWithRetry(commentsUrl, CommentSearchPT.class);
        } catch (HttpClientErrorException.TooManyRequests e) {
            // Si PeerTube rate-limita, devolvemos el vídeo igualmente (sin comentarios).
        }
        
        // Dentro de getVideoById, en el bucle de comentarios:

        if (commentResponse != null && commentResponse.getData() != null) {
            for (CommentPT ptComment : commentResponse.getData()) {
                Comment comentarioTransformado = transformer.transformComment(ptComment, video.getId());
        
                // ¡ESTE ES EL FILTRO CRUCIAL!
                // Solo añadimos el comentario si tiene texto real. 
                // Si el texto es null o está vacío (después de quitar espacios), lo ignoramos.
                if (comentarioTransformado != null && 
                    comentarioTransformado.getText() != null && 
                    !comentarioTransformado.getText().trim().isEmpty()) {
            
                    video.getComments().add(comentarioTransformado);
                } 
            }       
        }
        
        String captionsUrl = videoUrl + "/captions";
        CaptionSearchPT captionResponse = null;
        try {
            captionResponse = getForObjectWithRetry(captionsUrl, CaptionSearchPT.class);
        } catch (HttpClientErrorException.TooManyRequests e) {
            // Si PeerTube rate-limita, devolvemos el vídeo igualmente (sin captions).
        }
        
        if (captionResponse != null && captionResponse.getData() != null) {
            for (CaptionPT ptCaption : captionResponse.getData()) {
                Caption caption = transformer.transformCaption(ptCaption, video.getId());
                video.getCaptions().add(caption); 
            }
        }

        return video;
    }
}
