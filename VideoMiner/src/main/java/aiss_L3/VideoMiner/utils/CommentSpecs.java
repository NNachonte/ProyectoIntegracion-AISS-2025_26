package aiss_L3.VideoMiner.utils;

import org.springframework.data.jpa.domain.Specification;

import aiss_L3.VideoMiner.model.Comment;

public class CommentSpecs {
    public static Specification<Comment> textContains(String text) {
        return (root, query, builder) -> 
            text == null || text.isEmpty() ? builder.conjunction() : 
            builder.like(builder.lower(root.get("text")), "%" + text.toLowerCase() + "%");
    }
}
