package aiss_L3.TwitchMiner.controller;

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
import org.springframework.web.client.HttpClientErrorException;

import aiss_L3.TwitchMiner.model.videominer.Channel;
import aiss_L3.TwitchMiner.services.ChannelService;
import aiss_L3.TwitchMiner.exception.ResourceNotFoundException;

@Tag(name = "Twitch Channels", description = "Twitch channel mining API")
@RestController
@RequestMapping("/twitch/channels")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @GetMapping
    @Operation(tags = {"get", "channels"}, summary = "Get Twitch channels", description = "Returns channels obtained from Twitch.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Channels retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "502", description = "Error returned by the Twitch API", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public List<Channel> getChannels(
            @Parameter(description = "Search query for Twitch channels. Uses the configured default when omitted.", required = false)
            @RequestParam(value = "query", required = false) String query,
            @Parameter(description = "Maximum number of channels to mine. Default: 10", required = false)
            @RequestParam(value = "maxResults", required = false, defaultValue = "10") Integer maxResults) {
        return channelService.getChannels(query, maxResults);
    }

    @GetMapping("/{id}")
    @Operation(tags = {"get", "channels"}, summary = "Get Twitch channel by ID", description = "Returns a Twitch channel using its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Channel retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Channel not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "502", description = "Error returned by the Twitch API", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public Channel getChannelById(
            @Parameter(description = "Twitch channel identifier", required = true)
            @PathVariable("id") String id,
            @Parameter(description = "Maximum number of videos to mine. Default: 10", required = false)
            @RequestParam(value = "maxVideos", required = false, defaultValue = "10") Integer maxVideos) {
        try {
            return channelService.getChannelById(id, maxVideos);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() != null && e.getStatusCode().value() == 400
                    && e.getMessage() != null && e.getMessage().contains("Bad Identifiers")) {
                throw new ResourceNotFoundException("Channel not found on Twitch: " + id);
            }
            throw e;
        }
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(tags = {"post", "channels"}, summary = "Create Twitch channel in VideoMiner", description = "Mines a Twitch channel and sends it to VideoMiner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Channel created in VideoMiner successfully", content = {
                    @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Channel not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "502", description = "Error returned by the Twitch API or VideoMiner", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public Channel createChannelInVideoMiner(
            @Parameter(description = "Twitch channel identifier", required = true)
            @PathVariable("id") String id,
            @Parameter(description = "Maximum number of videos to mine. Default: 10", required = false)
            @RequestParam(value = "maxVideos", defaultValue = "10") Integer maxVideos) {
                try {
                        return channelService.postChannel(id, maxVideos);
                } catch (HttpClientErrorException e) {
                        if (e.getStatusCode() != null && e.getStatusCode().value() == 400
                                        && e.getMessage() != null && e.getMessage().contains("Bad Identifiers")) {
                                throw new ResourceNotFoundException("Channel not found on Twitch: " + id);
                        }
                        throw e;
                }
    }
}
