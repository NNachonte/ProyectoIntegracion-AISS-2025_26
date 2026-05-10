package aiss_L3.VideoMiner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import aiss_L3.VideoMiner.controller.VideoController;
import aiss_L3.VideoMiner.model.Comment;
import aiss_L3.VideoMiner.model.Video;
import aiss_L3.VideoMiner.repository.VideoRepository;

@WebMvcTest(VideoController.class)
class VideoControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoRepository videoRepository;

    private static Video sampleVideo(String id) {
        Video video = new Video();
        video.setId(id);
        video.setName("Sample Video");
        video.setDescription("Description");
        video.setReleaseTime("2024-01-02T00:00:00Z");
        video.setComments(new ArrayList<>(List.of(sampleComment("comment-1"))));
        video.setCaptions(new ArrayList<>());
        return video;
    }

    private static Comment sampleComment(String id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText("[CLIP] Example clip\nhttps://example.com/clip");
        comment.setCreatedOn("2024-01-03T00:00:00Z");
        return comment;
    }

    @Test
    void getAllVideos_returnsStoredVideos() throws Exception {
        when(videoRepository.findAll()).thenReturn(List.of(sampleVideo("video-1")));

        mockMvc.perform(get("/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].comments[0].id").value("comment-1"));

        verify(videoRepository).findAll();
    }

    @Test
    void getVideoById_returnsVideo() throws Exception {
        when(videoRepository.findById("video-1")).thenReturn(Optional.of(sampleVideo("video-1")));

        mockMvc.perform(get("/videos/{id}", "video-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("video-1"));

        verify(videoRepository).findById("video-1");
    }
}