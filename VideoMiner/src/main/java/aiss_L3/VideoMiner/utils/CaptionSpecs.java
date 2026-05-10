package aiss_L3.VideoMiner.utils;

import org.springframework.data.jpa.domain.Specification;

import aiss_L3.VideoMiner.model.Caption;

public class CaptionSpecs {
    public static Specification<Caption> languageEquals(String language) {
        return (root, query, builder) -> 
            language == null || language.isEmpty() ? builder.conjunction() : 
            builder.equal(builder.lower(root.get("language")), language.toLowerCase());
    }
}
