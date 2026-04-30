package aiss_L3.VideoMiner.controller;

import aiss_L3.VideoMiner.exception.VideoNotFoundException;
import aiss_L3.VideoMiner.model.Video;
import aiss_L3.VideoMiner.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    VideoRepository videoRepository;


    @GetMapping
    public List<Video> findAll() {
        return videoRepository.findAll();
    }


    @GetMapping("/{id}")
    public Video findById(@PathVariable String id) throws VideoNotFoundException {
        return videoRepository.findById(id)
                .orElseThrow(VideoNotFoundException::new);
    }
}