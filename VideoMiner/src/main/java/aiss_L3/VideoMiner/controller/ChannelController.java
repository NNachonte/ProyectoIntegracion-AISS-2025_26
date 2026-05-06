package aiss_L3.VideoMiner.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import aiss_L3.VideoMiner.exception.ChannelNotFoundException;
import aiss_L3.VideoMiner.model.Channel;
import aiss_L3.VideoMiner.repository.ChannelRepository;

@Tag(name = "Channel", description = "Channel management API")
@RestController
@RequestMapping("/channels")
public class ChannelController {

    @Autowired
    ChannelRepository channelRepository;


    @GetMapping
    @Operation(tags = {"get", "channels"}, summary = "Get all channels", description = "Returns all channels stored in VideoMiner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Channels retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }


    @GetMapping("/{id}")
    @Operation(tags = {"get", "channels"}, summary = "Get channel by ID", description = "Returns a channel using its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Channel retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Channel not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public Channel findById(
            @Parameter(description = "Channel identifier", required = true)
            @PathVariable String id) throws ChannelNotFoundException {
        return channelRepository.findById(id)
                .orElseThrow(ChannelNotFoundException::new);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(tags = {"post", "channels"}, summary = "Create channel", description = "Creates a new channel in VideoMiner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Channel created successfully", content = {
                    @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Invalid channel data", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "415", description = "Unsupported media type", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public Channel create(
            @Parameter(description = "Channel data", required = true)
            @Valid @RequestBody Channel channel) {
        return channelRepository.save(channel);
    }
}
