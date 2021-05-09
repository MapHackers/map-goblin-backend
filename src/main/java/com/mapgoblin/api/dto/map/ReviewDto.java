package com.mapgoblin.api.dto.map;

import com.mapgoblin.domain.Review;

public class ReviewDto {

    private String content;

    public ReviewDto(Review review){
        this.content = review.getContent();
    }
}
