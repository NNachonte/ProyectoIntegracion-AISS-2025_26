package aiss_L3.VideoMiner.utils;

import org.springframework.data.jpa.domain.Specification;

import aiss_L3.VideoMiner.model.Video;

public class VideoSpecs {
    public static Specification<Video> nameContains(String name) {
        return (root, query, builder) -> 
            name == null || name.isEmpty() ? builder.conjunction() : 
            builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
    public static Specification<Video> descriptionContains(String description) {
        return (root, query, builder) -> 
            description == null || description.isEmpty() ? builder.conjunction() : 
            builder.like(builder.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }
}


