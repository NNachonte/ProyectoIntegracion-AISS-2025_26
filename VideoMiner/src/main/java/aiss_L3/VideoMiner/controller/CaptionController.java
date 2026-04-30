package aiss_L3.VideoMiner.controller;

import aiss_L3.VideoMiner.exception.CaptionNotFoundException;
import aiss_L3.VideoMiner.exception.VideoNotFoundException;
import aiss_L3.VideoMiner.model.Caption;
import aiss_L3.VideoMiner.model.Video;
import aiss_L3.VideoMiner.repository.CaptionRepository;
import aiss_L3.VideoMiner.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CaptionController {

    @Autowired
    CaptionRepository captionRepository;

    @Autowired
    VideoRepository videoRepository;


    @GetMapping("/captions")
    public List<Caption> findAll() {
        return captionRepository.findAll();
    }


    @GetMapping("/captions/{id}")
    public Caption findById(@PathVariable String id) throws CaptionNotFoundException {
        return captionRepository.findById(id)
                .orElseThrow(CaptionNotFoundException::new);
    }


    @GetMapping("/videos/{videoId}/captions")
    public List<Caption> findByVideo(@PathVariable String videoId)
            throws VideoNotFoundException {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(VideoNotFoundException::new);
        return video.getCaptions();
    }
}