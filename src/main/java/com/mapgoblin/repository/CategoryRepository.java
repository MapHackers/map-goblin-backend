package com.mapgoblin.repository;

import com.mapgoblin.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<List<Category>> findByName(String name);
}
