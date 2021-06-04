package com.mapgoblin.repository;

import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.SpaceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpaceCategoryRepository extends JpaRepository<SpaceCategory, Long> {

    Optional<List<SpaceCategory>> findBySpace(Space space);
    Optional<SpaceCategory> findByCategoryId(Long id);
}
