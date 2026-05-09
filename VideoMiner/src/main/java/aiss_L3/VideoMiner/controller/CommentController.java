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

import aiss_L3.VideoMiner.exception.CommentNotFoundException;
import aiss_L3.VideoMiner.exception.VideoNotFoundException;
import aiss_L3.VideoMiner.model.Comment;
import aiss_L3.VideoMiner.model.Video;
import aiss_L3.VideoMiner.repository.CommentRepository;
import aiss_L3.VideoMiner.repository.VideoRepository;
import aiss_L3.VideoMiner.utils.CommentSpecs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Comment", description = "Comment management API")
@RestController
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    VideoRepository videoRepository;


    @GetMapping("/comments")
    @Operation(tags = {"get", "comments"}, summary = "Get all comments", description = "Returns all comments stored in VideoMiner. Supports filtering, pagination and sorting.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public List<Comment> findAll(
            @Parameter(description = "Filter by comment text") @RequestParam(required = false) String text,
            @Parameter(description = "Index of the first element") @RequestParam(defaultValue = "0") int offset,
            @Parameter(description = "Maximum elements to return") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Sorting criteria (e.g., +id, -createdOn)") @RequestParam(required = false) String sort
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

        // 3. FILTROS (Al ser solo uno, lo asignamos directo sin 'allOf' ni 'where' para evitar warnings)
        Specification<Comment> spec = CommentSpecs.textContains(text);

        // 4. CONSULTA Y RETORNO
        Page<Comment> page = commentRepository.findAll(spec, pageable);
        return page.getContent();
    }


    @GetMapping("/comments/{id}")
    @Operation(tags = {"get", "comments"}, summary = "Get comment by ID", description = "Returns a comment using its identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Comment not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public Comment findById(
            @Parameter(description = "Comment identifier", required = true)
            @PathVariable String id) throws CommentNotFoundException {
        return commentRepository.findById(id)
                .orElseThrow(CommentNotFoundException::new);
    }


    @GetMapping("/videos/{videoId}/comments")
    @Operation(tags = {"get", "videos", "comments"}, summary = "Get comments by video", description = "Returns the comments associated with a video.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully", content = {
                    @Content(schema = @Schema(implementation = Comment.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Video not found", content = {
                    @Content(schema = @Schema())
            }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                    @Content(schema = @Schema())
            })
    })
    public List<Comment> findByVideo(
            @Parameter(description = "Video identifier", required = true)
            @PathVariable String videoId)
            throws VideoNotFoundException {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(VideoNotFoundException::new);
        return video.getComments();
    }
}
