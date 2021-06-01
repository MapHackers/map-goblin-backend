package com.mapgoblin.service;

import com.mapgoblin.domain.Review;
import com.mapgoblin.domain.mapdata.MapData;
import com.mapgoblin.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public void saveReview(Review review){
        reviewRepository.save(review);
    }

    public List<Review> findByMapDataId(Long id){
        System.out.println("=========================================" +
                "FindbyMapdataID Service");
        return reviewRepository.findByMapDataId(id)
                .orElse(null);
    }

    public List<Review> findByMapData(MapData mapdata){
        return reviewRepository.findByMapData(mapdata)
                .orElse(null);
    }
}
