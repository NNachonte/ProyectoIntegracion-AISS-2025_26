package aiss_L3.PeerTubeMiner.videominer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import aiss_L3.PeerTubeMiner.model.videominer.Video;
import aiss_L3.PeerTubeMiner.services.videominer.VideoService;

@SpringBootTest
public class VideoServiceTest {

    @Autowired
    VideoService videoService;

    @Test
    @DisplayName("Test GET videos")     
    public void testGetVideos() {
        List<Video> videos;   
        videos = videoService.getVideos();

        assertFalse(videos.isEmpty(), "La lista de videos no debe estar vacía");

        System.out.println("GET videos: " + videos);
        System.out.println("Tamaño de la respuesta: " + videos.size());
    }

    @Test
    @DisplayName("Test GET video by ID")
    public void testGetVideoById() {
        String videoId = "dfd70b83-639f-4980-94af-304a56ab4b35"; 
        Video video = videoService.getVideoById(videoId);

        assertNotNull(video, "La información del video no debe estar vacía");

        System.out.println("GET video by ID: " + video); 
}
}
