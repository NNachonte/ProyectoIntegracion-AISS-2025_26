package aiss_L3.PeerTubeMiner.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aiss_L3.PeerTubeMiner.model.videominer.Video;
import aiss_L3.PeerTubeMiner.services.VideoService;

@RestController
@RequestMapping("/peertube/videos")
public class VideoController {
    @Autowired
    VideoService videoService;

    @GetMapping
    public List<Video> getVideos() {
        return videoService.getVideos();
    }

    @GetMapping("/{id}")
    public Video getVideoById(@PathVariable String id) {
        return videoService.getVideoById(id,2 );
    }
}
