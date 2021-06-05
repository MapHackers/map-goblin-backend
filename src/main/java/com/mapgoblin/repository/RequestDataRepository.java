package com.mapgoblin.repository;

import com.mapgoblin.domain.Request;
import com.mapgoblin.domain.RequestData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestDataRepository extends JpaRepository<RequestData, Long> {

    Optional<RequestData> findByMapDataIdAndLayerId(Long dataId, Long layerId);

    Optional<List<RequestData>> findByRequest(Request request);
}
