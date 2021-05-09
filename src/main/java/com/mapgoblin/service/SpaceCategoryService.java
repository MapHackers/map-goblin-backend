package com.mapgoblin.service;

import com.mapgoblin.repository.SpaceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpaceCategoryService {

    private final SpaceCategoryRepository spaceCategoryRepository;
}
