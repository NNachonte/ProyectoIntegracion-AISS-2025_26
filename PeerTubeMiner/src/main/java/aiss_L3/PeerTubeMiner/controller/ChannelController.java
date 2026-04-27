package aiss_L3.PeerTubeMiner.controller;

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

import aiss_L3.PeerTubeMiner.model.videominer.Channel;
import aiss_L3.PeerTubeMiner.services.ChannelService;

@RestController
@RequestMapping("/peertube/channels")
public class ChannelController {
    @Autowired
    ChannelService channelService;

    @GetMapping
    public List<Channel> getChannels() {
        return channelService.getChannels();
    }

    @GetMapping("/{id}")
    public Channel getChannelById(@PathVariable String id) {
        return channelService.getChannelById(id);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Channel createChannelInVideoMiner(
        @PathVariable String id,
        @RequestParam(defaultValue = "10") Integer maxVideos,
        @RequestParam(defaultValue = "2") Integer maxComments) {
    
        return channelService.postChannel(id, maxVideos, maxComments);
    }
}