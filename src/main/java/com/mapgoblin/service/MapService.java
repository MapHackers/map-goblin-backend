package com.mapgoblin.service;

import com.mapgoblin.domain.Map;
import com.mapgoblin.repository.MapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MapService {

    private final MapRepository mapRepository;

    /**
     * Find map information with mapId
     *
     * @param mapId
     * @return
     */
    public Map findByMapId(Long mapId) {
        return mapRepository.findById(mapId)
                .orElse(null);
    }

    public void save(Map map){
        mapRepository.save(map);
    }
}
