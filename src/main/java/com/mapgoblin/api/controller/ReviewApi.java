package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.map.ReviewDto;
import com.mapgoblin.domain.Layer;
import com.mapgoblin.domain.Map;
import com.mapgoblin.domain.Review;
import com.mapgoblin.domain.mapdata.MapData;
import com.mapgoblin.domain.mapdata.Point;
import com.mapgoblin.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewApi {

    private final MapDataService mapDataService;
    private final ReviewService reviewService;
    private final LayerService layerService;
    private final PointService pointService;
    /**
     * Add review in Mapdata
     *
     * @return
     */
    @PostMapping()
    public ResponseEntity<?> insert(@RequestBody ReviewDto request){
        //
        Layer layer = layerService.findByLayerNameAndMapId(request.getLayerName(), request.getMapId());
        Point point = pointService.findByGeometryAndLayerId(request.getGeometry(), layer.getId());

        MapData mapData = mapDataService.findById(point.getId());

        if(mapData == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Review review = Review.createReview(mapData, request.getContent(), request.getRating(), request.getAuthor());
        reviewService.saveReview(review);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get review by mapDataId
     *
     * @return
     */
    @PostMapping("/mapData")
    public ResponseEntity<?> find(@RequestBody ReviewDto request){

        Layer layer = layerService.findByLayerNameAndMapId(request.getLayerName(), request.getMapId());
        MapData mapData = pointService.findByGeometryAndLayerId(request.getGeometry(), layer.getId());


//        List<Review> reviewList = reviewService.findByMapDataId(point.getId());
        List<Review> reviewList = reviewService.findByMapData(mapData);

        ArrayList<ReviewDto> responseReviewList = new ArrayList<>();

        if(reviewList.size() == 0){
            return ResponseEntity.ok(new ApiResult<>(responseReviewList));
        }
        System.err.println("=-----------------------------");
        System.err.println("size : " + reviewList.size());
        System.err.println(reviewList.get(0).getContent());
        for(Review review: reviewList){
            ReviewDto reviewDto = new ReviewDto();
            reviewDto.setContent(review.getContent());
            reviewDto.setRating(review.getRating());
            reviewDto.setAuthor(review.getAuthor());
            responseReviewList.add(reviewDto);
        }
        return ResponseEntity.ok(new ApiResult<>(responseReviewList));
    }
}
