package com.mapgoblin.api.dto.map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateMapDataRequest {
    private Long mapId;
    private String layerName;
    private String title;
    private String description;
    private Float rating;
    private String geometry;
    private String mapDataType;
    private String thumbnail;
}
