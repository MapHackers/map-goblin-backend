package com.mapgoblin.repository;

import com.mapgoblin.domain.Request;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Page<Request> findBySpaceAndStatus(Space space, RequestStatus status, Pageable pageable);
}
