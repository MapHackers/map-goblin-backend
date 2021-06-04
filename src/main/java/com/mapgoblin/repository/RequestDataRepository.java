package com.mapgoblin.repository;

import com.mapgoblin.domain.RequestData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestDataRepository extends JpaRepository<RequestData, Long> {
}
