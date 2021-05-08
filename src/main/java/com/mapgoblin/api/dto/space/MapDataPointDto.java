package com.mapgoblin.api.dto.space;

import com.mapgoblin.domain.mapdata.Point;
import lombok.Data;

@Data
public class MapDataPointDto {
    private Long id;

    private String title;

    private String thumbnail;

    private String description;

    private Float rating;

    private String geometry;

    public MapDataPointDto(Point point){
        this.id = point.getId();
        this.title = point.getName();
        this.thumbnail = point.getThumbnail();
        this.description = point.getDescription();
        this.rating = point.getRating();
        this.geometry = point.getGeometry();
    }

}
