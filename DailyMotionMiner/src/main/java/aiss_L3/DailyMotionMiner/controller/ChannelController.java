package aiss_L3.DailyMotionMiner.controller;

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

import aiss_L3.DailyMotionMiner.model.videominer.Channel;
import aiss_L3.DailyMotionMiner.services.ChannelService;

@RestController
@RequestMapping("/dailymotion/channels")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    // GET /dailymotion/channels
    @GetMapping
    public List<Channel> getChannels() {
        return channelService.getChannels();
    }

    // GET /dailymotion/channels/{id}
    @GetMapping("/{id}")
    public Channel getChannelById(
            @PathVariable("id") String id,
            @RequestParam(value = "maxVideos", required = false, defaultValue = "10") Integer maxVideos,
            @RequestParam(value = "maxPages", required = false, defaultValue = "2") Integer maxPages) {
        return channelService.getChannelById(id, maxVideos, maxPages);
    }

    // POST /dailymotion/channels/{id} → envía el canal a VideoMiner
    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Channel createChannelInVideoMiner(
            @PathVariable("id") String id,
            @RequestParam(value = "maxVideos", defaultValue = "10") Integer maxVideos,
            @RequestParam(value = "maxPages", defaultValue = "2") Integer maxPages) {
        return channelService.postChannel(id, maxVideos, maxPages);
    }
}
