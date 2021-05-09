package com.mapgoblin.api.dto.map;

import com.mapgoblin.domain.Layer;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class LayerDto {

    private Long id;

    private String name;

    private List<MapDataDto> mapDatas;

    public LayerDto(Layer layer){
        this.id = layer.getId();
        this.name = layer.getName();
        this.mapDatas = layer.getMapDataList()
                .stream()
                .map(mapData -> new MapDataDto(mapData))
                .collect(Collectors.toList());


    }
}
