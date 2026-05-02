package aiss_L3.DailyMotionMiner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import aiss_L3.DailyMotionMiner.model.videominer.Video;
import aiss_L3.DailyMotionMiner.services.VideoService;

@SpringBootTest
class VideoServiceTest {

    @Autowired
    VideoService videoService;

    @Autowired
    RestTemplate restTemplate;

    MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("Obtener un vídeo detallado con límite de comentarios (Dailymotion)")
    void getVideoById() {
        String videoId = "x1";
        Integer maxComments = 2;

        String videoUrl = "https://api.dailymotion.com/video/" + videoId
                + "?fields=id,title,description,created_time,channel,channel.name,channel.description,channel.created_time,owner,owner.screenname,owner.url,owner.avatar_720_url";

        String commentsUrl = "https://api.dailymotion.com/video/" + videoId
                + "/comments?fields=id,message,created_time&limit=" + maxComments;

        String captionsUrl = "https://api.dailymotion.com/video/" + videoId
                + "/subtitles?fields=id,language,language_label,url,format";

        String videoJson = "{"
                + "\"id\":\"x1\","
                + "\"title\":\"Test title\","
                + "\"description\":\"Test description\","
                + "\"created_time\":1700000000,"
                + "\"channel\":\"news\","
                + "\"channel.name\":\"News\","
                + "\"channel.description\":\"Channel desc\","
                + "\"channel.created_time\":1600000000,"
                + "\"owner\":\"123\","
                + "\"owner.screenname\":\"Alice\","
                + "\"owner.url\":\"https://dailymotion.com/user/alice\","
                + "\"owner.avatar_720_url\":\"https://img.example/avatar.jpg\""
                + "}";

        String commentsJson = "{"
                + "\"page\":1,\"limit\":" + maxComments + ",\"explicit\":false,\"total\":1,\"has_more\":false,"
                + "\"list\":[{\"id\":\"c1\",\"message\":\"hello\",\"created_time\":1700000001}]"
                + "}";

        String captionsJson = "{"
                + "\"page\":1,\"limit\":10,\"explicit\":false,\"total\":1,\"has_more\":false,"
                + "\"list\":[{\"id\":\"es\",\"language\":\"es\",\"language_label\":\"Spanish\",\"url\":\"https://subs.example/es.vtt\",\"format\":\"vtt\"}]"
                + "}";

        mockServer.expect(MockRestRequestMatchers.requestTo(videoUrl))
                .andExpect(MockRestRequestMatchers.method(org.springframework.http.HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(videoJson, MediaType.APPLICATION_JSON));

        mockServer.expect(MockRestRequestMatchers.requestTo(commentsUrl))
                .andExpect(MockRestRequestMatchers.method(org.springframework.http.HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(commentsJson, MediaType.APPLICATION_JSON));

        mockServer.expect(MockRestRequestMatchers.requestTo(captionsUrl))
                .andExpect(MockRestRequestMatchers.method(org.springframework.http.HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(captionsJson, MediaType.APPLICATION_JSON));

        Video video = videoService.getVideoById(videoId, maxComments);

        mockServer.verify();

        assertNotNull(video);
        assertEquals("x1", video.getId());
        assertEquals("Test title", video.getName());
        assertEquals("Test description", video.getDescription());
        assertEquals("2023-11-14T22:13:20Z", video.getReleaseTime());

        assertNotNull(video.getAuthor());
        assertEquals("Alice", video.getAuthor().getName());
        assertEquals("https://dailymotion.com/user/alice", video.getAuthor().getUser_link());
        assertEquals("https://img.example/avatar.jpg", video.getAuthor().getPicture_link());

        assertNotNull(video.getComments());
        assertTrue(video.getComments().size() <= maxComments);
        assertEquals("hello", video.getComments().get(0).getText());
        assertEquals("2023-11-14T22:13:21Z", video.getComments().get(0).getCreatedOn());

        assertNotNull(video.getCaptions());
        assertEquals(1, video.getCaptions().size());
        assertEquals("es", video.getCaptions().get(0).getId());
        assertEquals("Spanish", video.getCaptions().get(0).getName());
        assertEquals("es", video.getCaptions().get(0).getLanguage());
    }

    @Test
    @DisplayName("Listar vídeos globales (Dailymotion)")
    void getVideos() {
        String listUrl = "https://api.dailymotion.com/videos"
                + "?fields=id,title,description,created_time,channel,channel.name,channel.description,channel.created_time,owner,owner.screenname,owner.url,owner.avatar_720_url";

        String listJson = "{"
                + "\"page\":1,\"limit\":1,\"explicit\":false,\"total\":1,\"has_more\":false,"
                + "\"list\":[{\"id\":\"x1\",\"title\":\"Test title\",\"description\":\"Test description\",\"created_time\":1700000000,\"owner\":\"123\",\"owner.screenname\":\"Alice\"}]"
                + "}";

        String videoUrl = "https://api.dailymotion.com/video/x1"
                + "?fields=id,title,description,created_time,channel,channel.name,channel.description,channel.created_time,owner,owner.screenname,owner.url,owner.avatar_720_url";

        String commentsUrl = "https://api.dailymotion.com/video/x1"
                + "/comments?fields=id,message,created_time&limit=2";

        String captionsUrl = "https://api.dailymotion.com/video/x1"
                + "/subtitles?fields=id,language,language_label,url,format";

        String videoJson = "{"
                + "\"id\":\"x1\","
                + "\"title\":\"Test title\","
                + "\"description\":\"Test description\","
                + "\"created_time\":1700000000,"
                + "\"owner\":\"123\","
                + "\"owner.screenname\":\"Alice\""
                + "}";

        String commentsJson = "{\"page\":1,\"limit\":2,\"explicit\":false,\"total\":0,\"has_more\":false,\"list\":[]}";
        String captionsJson = "{\"page\":1,\"limit\":10,\"explicit\":false,\"total\":0,\"has_more\":false,\"list\":[]}";

        mockServer.expect(MockRestRequestMatchers.requestTo(listUrl))
                .andExpect(MockRestRequestMatchers.method(org.springframework.http.HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(listJson, MediaType.APPLICATION_JSON));

        mockServer.expect(MockRestRequestMatchers.requestTo(videoUrl))
                .andExpect(MockRestRequestMatchers.method(org.springframework.http.HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(videoJson, MediaType.APPLICATION_JSON));

        mockServer.expect(MockRestRequestMatchers.requestTo(commentsUrl))
                .andExpect(MockRestRequestMatchers.method(org.springframework.http.HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(commentsJson, MediaType.APPLICATION_JSON));

        mockServer.expect(MockRestRequestMatchers.requestTo(captionsUrl))
                .andExpect(MockRestRequestMatchers.method(org.springframework.http.HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(captionsJson, MediaType.APPLICATION_JSON));

        List<Video> videos = videoService.getVideos();
        mockServer.verify();

        assertNotNull(videos);
        assertEquals(1, videos.size());
        assertEquals("x1", videos.get(0).getId());
        assertEquals("Test title", videos.get(0).getName());
        assertNotNull(videos.get(0).getComments());
        assertNotNull(videos.get(0).getCaptions());

        System.out.println("Ejemplo de vídeo obtenido: " + videos.get(0).toString());
    }
}
