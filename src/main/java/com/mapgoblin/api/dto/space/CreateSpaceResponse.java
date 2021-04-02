package com.mapgoblin.api.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateSpaceResponse {

    private Long id;

    private Long map_id;

    private String name;

    private String thumbnail;

    private String description;

    private int likeCount;

    private int dislikeCount;
}
