package aiss_L3.VideoMiner.controller;

import aiss_L3.VideoMiner.exception.CommentNotFoundException;
import aiss_L3.VideoMiner.exception.VideoNotFoundException;
import aiss_L3.VideoMiner.model.Comment;
import aiss_L3.VideoMiner.model.Video;
import aiss_L3.VideoMiner.repository.CommentRepository;
import aiss_L3.VideoMiner.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    VideoRepository videoRepository;


    @GetMapping("/comments")
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }


    @GetMapping("/comments/{id}")
    public Comment findById(@PathVariable String id) throws CommentNotFoundException {
        return commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
    }


    @GetMapping("/videos/{videoId}/comments")
    public List<Comment> findByVideo(@PathVariable String videoId)
            throws VideoNotFoundException {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(VideoNotFoundException::new);
        return video.getComments();
    }
}