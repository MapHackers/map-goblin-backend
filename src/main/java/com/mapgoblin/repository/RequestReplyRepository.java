package com.mapgoblin.repository;

import com.mapgoblin.domain.Request;
import com.mapgoblin.domain.RequestReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestReplyRepository extends JpaRepository<RequestReply, Long> {

    Optional<List<RequestReply>> findByRequest(Request request);
}
