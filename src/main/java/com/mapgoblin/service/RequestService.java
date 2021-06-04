package com.mapgoblin.service;

import com.mapgoblin.api.dto.request.RequestDto;
import com.mapgoblin.domain.Request;
import com.mapgoblin.domain.Space;
import com.mapgoblin.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;

    public Page<RequestDto> findRequestsOfSpace(Space space, Pageable pageable) {

        Page<Request> requests = requestRepository.findBySpace(space, pageable);

        return requests.map(RequestDto::new);
    }
}
