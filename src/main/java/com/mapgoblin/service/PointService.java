package com.mapgoblin.service;

import com.mapgoblin.api.dto.map.CreateMapDataRequest;
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

    public Point findByGeometryAndLayerId(String geometry, Long LayerId){
        return pointRepository.findByGeometryAndLayerId(geometry, LayerId)
                .orElse(null);
    }

    @Transactional
    public void modify(Long pointId, CreateMapDataRequest request){
        Point findPoint = pointRepository.findById(pointId).orElse(null);

        findPoint.setName(request.getTitle());
        findPoint.setDescription(request.getDescription());
    }

    public void delete(Point point){
        pointRepository.delete(point);
    }
}
