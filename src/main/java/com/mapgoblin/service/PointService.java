package com.mapgoblin.service;

import com.mapgoblin.domain.mapdata.Point;
import com.mapgoblin.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class PointService {

    private final PointRepository pointRepository;

    public Point findByGeometry(String geometry){
        return pointRepository.findByGeometry(geometry)
                .orElse(null);

    }

    public void delete(Point point){
        pointRepository.delete(point);
    }
}
