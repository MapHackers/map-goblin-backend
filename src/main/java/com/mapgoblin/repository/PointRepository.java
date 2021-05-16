package com.mapgoblin.repository;

import com.mapgoblin.domain.Layer;
import com.mapgoblin.domain.mapdata.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {

    Optional<Point> findByGeometry(String geometry);
    Optional<Point> findByGeometryAndLayerId(String geometry, Long LayerId);
}
