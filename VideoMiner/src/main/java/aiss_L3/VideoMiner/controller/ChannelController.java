package aiss_L3.VideoMiner.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aiss_L3.VideoMiner.exception.ChannelNotFoundException;
import aiss_L3.VideoMiner.model.Channel;
import aiss_L3.VideoMiner.repository.ChannelRepository;
import aiss_L3.VideoMiner.utils.ChannelSpecs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Channel", description = "Channel management API")
@RestController
@RequestMapping("/channels")
public class ChannelController {

    @Autowired
    ChannelRepository channelRepository;


    @GetMapping
    @Operation(tags = {"get", "channels"}, summary = "Get all channels", description = "Returns all channels stored in VideoMiner. Supports filtering, pagination and sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Channels retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public List<Channel> findAll(
            @Parameter(description = "Filter by channel name") 
            @RequestParam(required = false) String name,
            
            @Parameter(description = "Filter by channel description") 
            @RequestParam(required = false) String description,
            
            @Parameter(description = "Index of the first element to return") 
            @RequestParam(defaultValue = "0") int offset,
            
            @Parameter(description = "Maximum number of elements to return") 
            @RequestParam(defaultValue = "10") int limit,
            
            @Parameter(description = "Sorting criteria (e.g., +name, -createdTime)") 
            @RequestParam(required = false) String sort
    ) {
        
        // --- 1. GESTIÓN DE ORDENACIÓN (+ y -) ---
        Sort springSort = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            List<Sort.Order> orders = new ArrayList<>();
            String[] sortParams = sort.split(","); 
            
            for (String param : sortParams) {
                param = param.trim();
                if (param.startsWith("-")) {
                    // Orden descendente (quita el signo menos)
                    orders.add(Sort.Order.desc(param.substring(1)));
                } else if (param.startsWith("+")) {
                    // Orden ascendente (quita el signo más)
                    orders.add(Sort.Order.asc(param.substring(1)));
                } else {
                    // Orden ascendente por defecto si no lleva signo
                    orders.add(Sort.Order.asc(param));
                }
            }
            springSort = Sort.by(orders);
        }

        // --- 2. GESTIÓN DE PAGINACIÓN (offset y limit) ---
        // Spring Data JPA requiere un número de página. Lo calculamos dividiendo el offset por el limit.
        // Ej: Si limit=10 y offset=20, entonces pageNumber = 2 (saltamos las páginas 0 y 1).
        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit, springSort);

        // --- 3. GESTIÓN DE FILTROS ---
        Specification<Channel> spec = Specification.allOf(
        ChannelSpecs.nameContains(name),
        ChannelSpecs.descriptionContains(description)
);

        // --- 4. EJECUCIÓN Y DEVOLUCIÓN DE LISTA ---
        Page<Channel> page = channelRepository.findAll(spec, pageable);
        return page.getContent();
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