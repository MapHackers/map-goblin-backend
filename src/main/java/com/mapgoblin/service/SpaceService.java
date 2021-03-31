package com.mapgoblin.service;

import com.mapgoblin.domain.Space;
import com.mapgoblin.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SpaceService {

    private final SpaceRepository spaceRepository;

    public List<Space> findAll() {
        return spaceRepository.findAll();
    }
}
