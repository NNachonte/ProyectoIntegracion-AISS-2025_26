package aiss_L3.DailyMotionMiner.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aiss_L3.DailyMotionMiner.model.videominer.Channel;
import aiss_L3.DailyMotionMiner.services.ChannelService;

@Tag(name = "DailyMotion Channels", description = "DailyMotion channel mining API")
@RestController
@RequestMapping("/dailymotion/channels")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @GetMapping
    @Operation(tags = {"get", "channels"}, summary = "Get DailyMotion channels", description = "Returns channels obtained from DailyMotion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Channels retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "502", description = "Error returned by the DailyMotion API", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public List<Channel> getChannels() {
        return channelService.getChannels();
    }

    @GetMapping("/{id}")
    @Operation(tags = {"get", "channels"}, summary = "Get DailyMotion channel by ID", description = "Returns a DailyMotion channel using its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Channel retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Channel not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "502", description = "Error returned by the DailyMotion API", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public Channel getChannelById(
            @Parameter(description = "DailyMotion channel identifier", required = true)
            @PathVariable("id") String id,
            @Parameter(description = "Maximum number of videos to mine. Default: 10", required = false)
            @RequestParam(value = "maxVideos", required = false, defaultValue = "10") Integer maxVideos,
            @Parameter(description = "Maximum number of pages to request from DailyMotion. Default: 2", required = false)
            @RequestParam(value = "maxPages", required = false, defaultValue = "2") Integer maxPages) {
        return channelService.getChannelById(id, maxVideos, maxPages);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(tags = {"post", "channels"}, summary = "Create DailyMotion channel in VideoMiner", description = "Mines a DailyMotion channel and sends it to VideoMiner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Channel created in VideoMiner successfully", content = {
                    @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Channel not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "502", description = "Error returned by the DailyMotion API or VideoMiner", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public Channel createChannelInVideoMiner(
            @Parameter(description = "DailyMotion channel identifier", required = true)
            @PathVariable("id") String id,
            @Parameter(description = "Maximum number of videos to mine. Default: 10", required = false)
            @RequestParam(value = "maxVideos", defaultValue = "10") Integer maxVideos,
            @Parameter(description = "Maximum number of pages to request from DailyMotion. Default: 2", required = false)
            @RequestParam(value = "maxPages", defaultValue = "2") Integer maxPages) {
        return channelService.postChannel(id, maxVideos, maxPages);
    }
}
