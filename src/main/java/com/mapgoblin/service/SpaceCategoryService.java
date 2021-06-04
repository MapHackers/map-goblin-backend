package com.mapgoblin.service;

import com.mapgoblin.domain.SpaceCategory;
import com.mapgoblin.repository.SpaceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpaceCategoryService {

    private final SpaceCategoryRepository spaceCategoryRepository;

    public SpaceCategory findByCategoryId(Long id){
        return spaceCategoryRepository.findByCategoryId(id)
                .orElse(null);
    }
}
