package aiss_L3.VideoMiner.controller;

import aiss_L3.VideoMiner.exception.VideoNotFoundException;
import aiss_L3.VideoMiner.model.Video;
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

@Tag(name = "Video", description = "Video management API")
@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    VideoRepository videoRepository;


    @GetMapping
    @Operation(tags = {"get", "videos"}, summary = "Get all videos", description = "Returns all videos stored in VideoMiner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Videos retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Video.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public List<Video> findAll() {
        return videoRepository.findAll();
    }


    @GetMapping("/{id}")
    @Operation(tags = {"get", "videos"}, summary = "Get video by ID", description = "Returns a video using its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Video.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Video not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public Video findById(
            @Parameter(description = "Video identifier", required = true)
            @PathVariable String id) throws VideoNotFoundException {
        return videoRepository.findById(id)
                .orElseThrow(VideoNotFoundException::new);
    }
}
