package aiss_L3.VideoMiner.controller;

import aiss_L3.VideoMiner.exception.CaptionNotFoundException;
import aiss_L3.VideoMiner.exception.VideoNotFoundException;
import aiss_L3.VideoMiner.model.Caption;
import aiss_L3.VideoMiner.model.Video;
import aiss_L3.VideoMiner.repository.CaptionRepository;
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

@Tag(name = "Caption", description = "Caption management API")
@RestController
public class CaptionController {

    @Autowired
    CaptionRepository captionRepository;

    @Autowired
    VideoRepository videoRepository;


    @GetMapping("/captions")
    @Operation(tags = {"get", "captions"}, summary = "Get all captions", description = "Returns all captions stored in VideoMiner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Captions retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Caption.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public List<Caption> findAll() {
        return captionRepository.findAll();
    }


    @GetMapping("/captions/{id}")
    @Operation(tags = {"get", "captions"}, summary = "Get caption by ID", description = "Returns a caption using its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Caption retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Caption.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Caption not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public Caption findById(
            @Parameter(description = "Caption identifier", required = true)
            @PathVariable String id) throws CaptionNotFoundException {
        return captionRepository.findById(id)
                .orElseThrow(CaptionNotFoundException::new);
    }


    @GetMapping("/videos/{videoId}/captions")
    @Operation(tags = {"get", "videos", "captions"}, summary = "Get captions by video", description = "Returns the captions associated with a video.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Captions retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Caption.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Video not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public List<Caption> findByVideo(
            @Parameter(description = "Video identifier", required = true)
            @PathVariable String videoId)
            throws VideoNotFoundException {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(VideoNotFoundException::new);
        return video.getCaptions();
    }
}
