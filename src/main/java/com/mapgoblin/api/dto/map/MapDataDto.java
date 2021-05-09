package com.mapgoblin.api.dto.map;

import com.mapgoblin.domain.mapdata.MapData;
import com.mapgoblin.domain.mapdata.Point;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class MapDataDto {

    private Long id;

    private String name;

    private String description;

    private Float rating;

    private String thumbnail;

    private String latlng;

    private List<ReviewDto> reviews;

    public MapDataDto(MapData mapData){
        this.id = mapData.getId();
        this.name = mapData.getName();
        this.description = mapData.getDescription();
        this.rating = mapData.getRating();
        this.thumbnail = mapData.getThumbnail();
        this.latlng = ((Point)mapData).getGeometry();
        this.reviews = mapData.getReviews()
                .stream()
                .map(review -> new ReviewDto(review))
                .collect(Collectors.toList());
    }

}
