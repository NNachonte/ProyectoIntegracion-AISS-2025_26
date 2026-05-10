package aiss_L3.VideoMiner.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aiss_L3.VideoMiner.exception.CaptionNotFoundException;
import aiss_L3.VideoMiner.exception.VideoNotFoundException;
import aiss_L3.VideoMiner.model.Caption;
import aiss_L3.VideoMiner.model.Video;
import aiss_L3.VideoMiner.repository.CaptionRepository;
import aiss_L3.VideoMiner.repository.VideoRepository;
import aiss_L3.VideoMiner.utils.CaptionSpecs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Caption", description = "Caption management API")
@RestController
public class CaptionController {

    @Autowired
    CaptionRepository captionRepository;

    @Autowired
    VideoRepository videoRepository;


    @GetMapping("/captions")
    @Operation(tags = {"get", "captions"}, summary = "Get all captions", description = "Returns all captions stored in VideoMiner. Supports filtering, pagination and sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Captions retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Caption.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public List<Caption> findAll(
            @Parameter(description = "Filter by exact language (e.g., 'es')") @RequestParam(required = false) String language,
            @Parameter(description = "Index of the first element") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "Maximum elements to return") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Sorting criteria (e.g., +id, -language)") @RequestParam(required = false) String sort
    ) {
        
        // 1. ORDENACIÓN (+ y -)
        Sort springSort = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            List<Sort.Order> orders = new ArrayList<>();
            for (String param : sort.split(",")) {
                param = param.trim();
                if (param.startsWith("-")) orders.add(Sort.Order.desc(param.substring(1)));
                else if (param.startsWith("+")) orders.add(Sort.Order.asc(param.substring(1)));
                else orders.add(Sort.Order.asc(param));
            }
            springSort = Sort.by(orders);
        }

        // 2. PAGINACIÓN
        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit, springSort);

        // 3. FILTROS (Al ser solo uno, lo asignamos directamente para evitar el warning 'where' o 'allOf')
        Specification<Caption> spec = CaptionSpecs.languageEquals(language);

        // 4. CONSULTA Y RETORNO
        Page<Caption> page = captionRepository.findAll(spec, pageable);
        return page.getContent();
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