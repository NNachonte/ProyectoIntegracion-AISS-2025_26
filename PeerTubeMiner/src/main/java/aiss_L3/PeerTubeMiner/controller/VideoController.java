package aiss_L3.PeerTubeMiner.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import aiss_L3.PeerTubeMiner.model.videominer.Video;
import aiss_L3.PeerTubeMiner.services.VideoService;

@Tag(name = "PeerTube Videos", description = "PeerTube video mining API")
@RestController
@RequestMapping("/peertube/videos")
public class VideoController {
    @Autowired
    VideoService videoService;

    @GetMapping
    @Operation(tags = {"get", "videos"}, summary = "Get PeerTube videos", description = "Returns videos obtained from PeerTube.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Videos retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Video.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "502", description = "Error returned by the PeerTube API", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public List<Video> getVideos() {
        return videoService.getVideos();
    }

    @GetMapping("/{id}")
    @Operation(tags = {"get", "videos"}, summary = "Get PeerTube video by ID", description = "Returns a PeerTube video using its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Video.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Video not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "502", description = "Error returned by the PeerTube API", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public Video getVideoById(
            @Parameter(description = "PeerTube video identifier", required = true)
            @PathVariable String id) {
        return videoService.getVideoById(id, 2);
    }
}
