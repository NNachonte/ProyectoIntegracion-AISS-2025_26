package aiss_L3.VideoMiner;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import aiss_L3.VideoMiner.controller.ChannelController;
import aiss_L3.VideoMiner.model.Channel;
import aiss_L3.VideoMiner.model.Comment;
import aiss_L3.VideoMiner.model.Video;
import aiss_L3.VideoMiner.repository.ChannelRepository;

@WebMvcTest(ChannelController.class)
class ChannelControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChannelRepository channelRepository;

    private static Channel sampleChannel(String id) {
        Channel channel = new Channel();
        channel.setId(id);
        channel.setName("VideoMiner Channel");
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
    void getAllChannels_returnsStoredChannels() throws Exception {
        when(channelRepository.findAll()).thenReturn(List.of(sampleChannel("channel-1")));

        mockMvc.perform(get("/channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].videos[0].comments[0].id").value("comment-1"));

        verify(channelRepository).findAll();
    }

    @Test
    void postChannel_savesAndReturnsCreatedChannel() throws Exception {
        Channel channel = sampleChannel("channel-1");
        when(channelRepository.save(any(Channel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/channels")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(channel)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("channel-1"));

        verify(channelRepository).save(any(Channel.class));
    }
}