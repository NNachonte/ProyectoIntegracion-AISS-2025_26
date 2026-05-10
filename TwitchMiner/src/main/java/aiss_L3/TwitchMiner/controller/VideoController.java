package aiss_L3.TwitchMiner.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aiss_L3.TwitchMiner.model.videominer.Video;
import aiss_L3.TwitchMiner.services.VideoService;

@RestController
@RequestMapping("/twitch/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping
    public List<Video> getVideos(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "maxResults", required = false, defaultValue = "10") Integer maxResults) {
        return videoService.getVideos(query, maxResults);
    }

    @GetMapping("/{id}")
    public Video getVideoById(@PathVariable("id") String id) {
        return videoService.getVideoById(id);
    }
}
