package com.mapgoblin.api.dto.space;

import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.SpaceCategory;
import lombok.Data;

@Data
public class SpaceCategoryDto {
    private String name;

    public SpaceCategoryDto(SpaceCategory spaceCategory) {
        this.name = spaceCategory.getCategory().getName();
    }
}
