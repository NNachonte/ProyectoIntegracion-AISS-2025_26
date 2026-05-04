package aiss_L3.DailyMotionMiner;

import aiss_L3.DailyMotionMiner.controller.ChannelController;
import aiss_L3.DailyMotionMiner.model.videominer.Channel;
import aiss_L3.DailyMotionMiner.services.ChannelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
class ChannelControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChannelService channelService;

    private static Channel sampleChannel(String id) {
        Channel channel = new Channel();
        channel.setId(id);
        channel.setName("Sample");
        channel.setDescription("Desc");
        channel.setCreatedTime("2023-01-01T00:00:00Z");
        return channel;
    }

    @Test
    void getChannelById_usesDefaultsWhenMissingParams() throws Exception {
        String id = "news";
        when(channelService.getChannelById(id, 10, 2)).thenReturn(sampleChannel(id));

        mockMvc.perform(get("/dailymotion/channels/{id}", id))
                .andExpect(status().isOk());

        verify(channelService).getChannelById(id, 10, 2);
    }

    @Test
    void getChannelById_bindsParams() throws Exception {
        String id = "news";
        when(channelService.getChannelById(id, 5, 3)).thenReturn(sampleChannel(id));

        mockMvc.perform(get("/dailymotion/channels/{id}", id)
                        .queryParam("maxVideos", "5")
                        .queryParam("maxPages", "3"))
                .andExpect(status().isOk());

        verify(channelService).getChannelById(id, 5, 3);
    }

    @Test
    void postChannel_usesDefaultsWhenMissingParams() throws Exception {
        String id = "news";
        when(channelService.postChannel(id, 10, 2)).thenReturn(sampleChannel(id));

        mockMvc.perform(post("/dailymotion/channels/{id}", id))
                .andExpect(status().isCreated());

        verify(channelService).postChannel(id, 10, 2);
    }

    @Test
    void postChannel_bindsParams() throws Exception {
        String id = "news";
        when(channelService.postChannel(id, 7, 1)).thenReturn(sampleChannel(id));

        mockMvc.perform(post("/dailymotion/channels/{id}", id)
                        .queryParam("maxVideos", "7")
                        .queryParam("maxPages", "1"))
                .andExpect(status().isCreated());

        verify(channelService).postChannel(id, 7, 1);
    }
}
