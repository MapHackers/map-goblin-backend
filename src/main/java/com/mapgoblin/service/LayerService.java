package com.mapgoblin.service;

import com.mapgoblin.domain.Layer;
import com.mapgoblin.domain.Map;
import com.mapgoblin.repository.LayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class LayerService {

    private final LayerRepository layerRepository;


    /**
     * Find Layer information with layerId
     *
     * @param layerName
     * @return
     */
    public Layer findByLayerName(String layerName) {
        return layerRepository.findByName(layerName)
                .orElse(null);
    }

    public Layer findByMapId(Long mapId){
        return layerRepository.findByMapId(mapId)
                .orElse(null);
    }

    public void save(Layer layer){
        layerRepository.save(layer);
    }

}
