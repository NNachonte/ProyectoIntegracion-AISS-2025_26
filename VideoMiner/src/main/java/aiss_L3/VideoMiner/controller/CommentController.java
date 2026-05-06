package aiss_L3.VideoMiner.controller;

import aiss_L3.VideoMiner.exception.CommentNotFoundException;
import aiss_L3.VideoMiner.exception.VideoNotFoundException;
import aiss_L3.VideoMiner.model.Comment;
import aiss_L3.VideoMiner.model.Video;
import aiss_L3.VideoMiner.repository.CommentRepository;
import aiss_L3.VideoMiner.repository.VideoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment", description = "Comment management API")
@RestController
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    VideoRepository videoRepository;


    @GetMapping("/comments")
    @Operation(tags = {"get", "comments"}, summary = "Get all comments", description = "Returns all comments stored in VideoMiner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }


    @GetMapping("/comments/{id}")
    @Operation(tags = {"get", "comments"}, summary = "Get comment by ID", description = "Returns a comment using its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public Comment findById(
            @Parameter(description = "Comment identifier", required = true)
            @PathVariable String id) throws CommentNotFoundException {
        return commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
    }


    @GetMapping("/videos/{videoId}/comments")
    @Operation(tags = {"get", "videos", "comments"}, summary = "Get comments by video", description = "Returns the comments associated with a video.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Video not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public List<Comment> findByVideo(
            @Parameter(description = "Video identifier", required = true)
            @PathVariable String videoId)
            throws VideoNotFoundException {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(VideoNotFoundException::new);
        return video.getComments();
    }
}
