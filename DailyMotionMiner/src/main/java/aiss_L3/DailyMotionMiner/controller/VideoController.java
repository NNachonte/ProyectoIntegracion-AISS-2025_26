package aiss_L3.DailyMotionMiner.controller;

import aiss_L3.DailyMotionMiner.model.videominer.Video;
import aiss_L3.DailyMotionMiner.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dailymotion/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    // GET /dailymotion/videos
    @GetMapping
    public List<Video> getVideos() {
        return videoService.getVideos();
    }

    // GET /dailymotion/videos/{id}
    @GetMapping("/{id}")
    public Video getVideoById(
            @PathVariable("id") String id,
            @RequestParam(value = "maxComments", required = false, defaultValue = "2") Integer maxComments
    ) {
        return videoService.getVideoById(id, maxComments);
    }
}
