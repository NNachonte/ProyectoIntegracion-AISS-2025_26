package aiss_L3.TwitchMiner.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aiss_L3.TwitchMiner.model.videominer.Channel;
import aiss_L3.TwitchMiner.services.ChannelService;

@RestController
@RequestMapping("/twitch/channels")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @GetMapping
    public List<Channel> getChannels(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "maxResults", required = false, defaultValue = "10") Integer maxResults) {
        return channelService.getChannels(query, maxResults);
    }

    @GetMapping("/{id}")
    public Channel getChannelById(
            @PathVariable("id") String id,
            @RequestParam(value = "maxVideos", required = false, defaultValue = "10") Integer maxVideos) {
        return channelService.getChannelById(id, maxVideos);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Channel createChannelInVideoMiner(
            @PathVariable("id") String id,
            @RequestParam(value = "maxVideos", defaultValue = "10") Integer maxVideos) {
        return channelService.postChannel(id, maxVideos);
    }
}
