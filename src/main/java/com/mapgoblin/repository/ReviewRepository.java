package com.mapgoblin.repository;

import com.mapgoblin.domain.Review;
import com.mapgoblin.domain.mapdata.MapData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<List<Review>> findByMapDataId(Long mapDataId);
    Optional<List<Review>> findByMapData(MapData mapdata);
}
