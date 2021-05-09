package com.mapgoblin.repository;

import com.mapgoblin.domain.Layer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LayerRepository extends JpaRepository<Layer, Long> {

    Optional<Layer> findByName(String layerName);
    Optional<Layer> findByNameAndMapId(String layerNAme, Long mapId);
    Optional<List<Layer>> findByMapId(Long mapId);


}
