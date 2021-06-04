package com.mapgoblin.repository;

import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpaceRepository extends JpaRepository<Space, Long> {

    Optional<List<Space>> findByHost(Space space);

    Optional<List<Space>> findByName(String name);

    Optional<List<Space>> findByNameContaining(String keyword);

    Optional<List<Space>> findByDescriptionContaining(String keyword);
}
