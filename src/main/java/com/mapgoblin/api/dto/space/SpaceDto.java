package com.mapgoblin.api.dto.space;

import com.mapgoblin.domain.Map;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.SpaceCategory;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.LAZY;

@Data
public class SpaceDto {

    private Long id;

    private String name;

    private String thumbnail;

    private String description;

    private int likeCount;

    private int dislikeCount;

    private List<SpaceCategoryDto> categories;

    public SpaceDto(Space space) {
        this.id = space.getId();
        this.name = space.getName();
        this.thumbnail = space.getThumbnail();
        this.description = space.getDescription();
        this.likeCount = space.getLikeCount();
        this.dislikeCount = space.getDislikeCount();
        this.categories = space.getCategories()
                .stream()
                .map(spaceCategory -> new SpaceCategoryDto(spaceCategory))
                .collect(Collectors.toList());
    }
}
