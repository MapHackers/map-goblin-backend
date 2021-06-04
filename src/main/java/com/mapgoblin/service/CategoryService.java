package com.mapgoblin.service;

import com.mapgoblin.domain.Category;
import com.mapgoblin.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findByName(String name){
        return categoryRepository.findByName(name)
                .orElse(null);
    }
}
