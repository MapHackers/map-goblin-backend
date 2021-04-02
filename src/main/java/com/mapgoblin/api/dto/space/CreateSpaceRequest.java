package com.mapgoblin.api.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateSpaceRequest {
    private String name;
    private String thumbnail;
    private String description;
}
