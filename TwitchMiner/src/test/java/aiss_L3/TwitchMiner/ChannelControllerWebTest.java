package aiss_L3.TwitchMiner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import aiss_L3.TwitchMiner.controller.ChannelController;
import aiss_L3.TwitchMiner.model.videominer.Channel;
import aiss_L3.TwitchMiner.model.videominer.Comment;
import aiss_L3.TwitchMiner.model.videominer.Video;
import aiss_L3.TwitchMiner.services.ChannelService;

@WebMvcTest(ChannelController.class)
class ChannelControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChannelService channelService;

    private static Channel sampleChannel(String id) {
        Channel channel = new Channel();
        channel.setId(id);
        channel.setName("Sample Channel");
        channel.setDescription("Description");
        channel.setCreatedTime("2024-01-01T00:00:00Z");

        Video video = new Video();
        video.setId("video-1");
        video.setName("Sample Video");
        video.setDescription("Video description");
        video.setReleaseTime("2024-01-02T00:00:00Z");
        video.setComments(new ArrayList<>(List.of(sampleComment("comment-1"))));
        video.setCaptions(new ArrayList<>());

        channel.getVideos().add(video);
        return channel;
    }

    private static Comment sampleComment(String id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setText("[CLIP] Example clip\nhttps://example.com/clip");
        comment.setCreatedOn("2024-01-03T00:00:00Z");
        return comment;
    }

    @Test
    void getChannelById_usesDefaultMaxVideosWhenMissingParam() throws Exception {
        String id = "12826";
        when(channelService.getChannelById(id, 10)).thenReturn(sampleChannel(id));

        mockMvc.perform(get("/twitch/channels/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.videos[0].comments[0].id").value("comment-1"));

        verify(channelService).getChannelById(id, 10);
    }

    @Test
    void getChannelById_bindsMaxVideosParam() throws Exception {
        String id = "12826";
        when(channelService.getChannelById(id, 5)).thenReturn(sampleChannel(id));

        mockMvc.perform(get("/twitch/channels/{id}", id).queryParam("maxVideos", "5"))
                .andExpect(status().isOk());

        verify(channelService).getChannelById(id, 5);
    }

    @Test
    void postChannel_usesDefaultMaxVideosWhenMissingParam() throws Exception {
        String id = "12826";
        when(channelService.postChannel(id, 10)).thenReturn(sampleChannel(id));

        mockMvc.perform(post("/twitch/channels/{id}", id))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.videos[0].comments[0].text").value("[CLIP] Example clip\nhttps://example.com/clip"));

        verify(channelService).postChannel(id, 10);
    }

    @Test
    void postChannel_bindsMaxVideosParam() throws Exception {
        String id = "12826";
        when(channelService.postChannel(id, 7)).thenReturn(sampleChannel(id));

        mockMvc.perform(post("/twitch/channels/{id}", id).queryParam("maxVideos", "7"))
                .andExpect(status().isCreated());

        verify(channelService).postChannel(id, 7);
    }
}