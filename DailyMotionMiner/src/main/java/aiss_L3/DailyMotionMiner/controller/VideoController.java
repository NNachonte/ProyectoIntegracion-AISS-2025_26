package aiss_L3.DailyMotionMiner.controller;

import java.util.List;

import aiss_L3.DailyMotionMiner.model.videominer.Video;
import aiss_L3.DailyMotionMiner.services.VideoService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "DailyMotion Videos", description = "DailyMotion video mining API")
@RestController
@RequestMapping("/dailymotion/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping
    @Operation(tags = {"get", "videos"}, summary = "Get DailyMotion videos", description = "Returns videos obtained from DailyMotion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Videos retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Video.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "502", description = "Error returned by the DailyMotion API", content = {
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
    @Operation(tags = {"get", "videos"}, summary = "Get DailyMotion video by ID", description = "Returns a DailyMotion video using its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Video.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Video not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "502", description = "Error returned by the DailyMotion API", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public Video getVideoById(
            @Parameter(description = "DailyMotion video identifier", required = true)
            @PathVariable("id") String id,
            @Parameter(description = "Maximum number of comments to mine. Default: 2", required = false)
            @RequestParam(value = "maxComments", required = false, defaultValue = "2") Integer maxComments) {
        return videoService.getVideoById(id, maxComments);
    }
}
