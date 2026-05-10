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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aiss_L3.PeerTubeMiner.model.videominer.Channel;
import aiss_L3.PeerTubeMiner.services.ChannelService;

@Tag(name = "PeerTube Channels", description = "PeerTube channel mining API")
@RestController
@RequestMapping("/peertube/channels")
public class ChannelController {
    @Autowired
    ChannelService channelService;

    @GetMapping
    @Operation(tags = {"get", "channels"}, summary = "Get PeerTube channels", description = "Returns channels obtained from PeerTube.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Channels retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "502", description = "Error returned by the PeerTube API", content = {
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
    @Operation(tags = {"get", "channels"}, summary = "Get PeerTube channel by ID", description = "Returns a PeerTube channel using its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Channel retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Channel not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "502", description = "Error returned by the PeerTube API", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public Channel getChannelById(
            @Parameter(description = "PeerTube channel identifier", required = true)
            @PathVariable String id) {
        return channelService.getChannelById(id);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(tags = {"post", "channels"}, summary = "Create PeerTube channel in VideoMiner", description = "Mines a PeerTube channel and sends it to VideoMiner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Channel created in VideoMiner successfully", content = {
                    @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Channel not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "502", description = "Error returned by the PeerTube API or VideoMiner", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public Channel createChannelInVideoMiner(
            @Parameter(description = "PeerTube channel identifier", required = true)
            @PathVariable String id,
            @Parameter(description = "Maximum number of videos to mine. Default: 10", required = false)
            @RequestParam(defaultValue = "10") Integer maxVideos,
            @Parameter(description = "Maximum number of comments to mine per video. Default: 2", required = false)
            @RequestParam(defaultValue = "2") Integer maxComments) {

        return channelService.postChannel(id, maxVideos, maxComments);
    }
}
