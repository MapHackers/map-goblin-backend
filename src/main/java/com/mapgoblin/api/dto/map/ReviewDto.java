package com.mapgoblin.api.dto.map;

import com.mapgoblin.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewDto {

    private Long mapId;
    private String layerName;
    private String geometry;
    private String content;
    private Float rating;
    private String author;

    public ReviewDto(Review review){
        this.content = review.getContent();
        this.rating = review.getRating();
        this.author = review.getAuthor();
    }

    public ReviewDto() {

    }
}
