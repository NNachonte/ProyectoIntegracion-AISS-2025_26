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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aiss_L3.VideoMiner.exception.VideoNotFoundException;
import aiss_L3.VideoMiner.model.Video;
import aiss_L3.VideoMiner.repository.VideoRepository;
import aiss_L3.VideoMiner.utils.VideoSpecs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Video", description = "Video management API")
@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    VideoRepository videoRepository;


    @GetMapping
    @Operation(tags = {"get", "videos"}, summary = "Get all videos", description = "Returns all videos stored in VideoMiner. Supports filtering, pagination and sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Videos retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Video.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public List<Video> findAll(
            @Parameter(description = "Filter by video name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by video description") @RequestParam(required = false) String description,
            @Parameter(description = "Index of the first element") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "Maximum elements to return") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Sorting criteria (e.g., +name, -releaseTime)") @RequestParam(required = false) String sort
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

        // 3. FILTROS (Usando allOf para evitar el warning amarillo)
        Specification<Video> spec = Specification.allOf(
                VideoSpecs.nameContains(name),
                VideoSpecs.descriptionContains(description)
        );

        // 4. CONSULTA Y RETORNO
        Page<Video> page = videoRepository.findAll(spec, pageable);
        return page.getContent();
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