package com.mapgoblin.repository;

import com.mapgoblin.domain.Layer;
import com.mapgoblin.domain.mapdata.MapData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MapDataRepository extends JpaRepository<MapData, Long> {

    Optional<List<MapData>> findByLayerId(Long layerId);

    Optional<MapData> findByGeometryAndLayer(String geom, Layer layer);
}
