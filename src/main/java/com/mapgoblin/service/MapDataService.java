package com.mapgoblin.service;

import com.mapgoblin.api.dto.map.MapDataDto;
import com.mapgoblin.domain.Layer;
import com.mapgoblin.domain.mapdata.MapData;
import com.mapgoblin.domain.mapdata.Point;
import com.mapgoblin.repository.MapDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class MapDataService {
    private final MapDataRepository mapDataRepository;

    public void savePoint(Layer layer, Point point){
        mapDataRepository.save(point);
        layer.addMapData(point);
    }

    public List<MapData> findByLayerId(Long layerId){
        return mapDataRepository.findByLayerId(layerId)
                .orElse(null);
    }


}
