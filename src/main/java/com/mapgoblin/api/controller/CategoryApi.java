package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.space.SpaceDto;
import com.mapgoblin.domain.*;
import com.mapgoblin.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryApi {

    private final MemberService memberService;
    private final MemberSpaceService memberSpaceService;
    private final CategoryService categoryService;
    private final SpaceCategoryService spaceCategoryService;
    private final LikeService likeService;

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories(){

        List<HashMap<String, String>> result = new ArrayList<>();

        List<Category> categories = categoryService.findAll();

        if(categories != null){
            for (Category category : categories) {
                HashMap<String, String> categoryInfo = new HashMap<>();

                categoryInfo.put("value", category.getName());

                result.add(categoryInfo);
            }

            return ResponseEntity.ok(result);
        }else{
            return ApiResult.errorMessage("카테고리 목록이 없습니다.", HttpStatus.OK);
        }
    }

    /**
     * Get specific category spaces
     *
     * @Return
     */
    @GetMapping("/{categoryName}/spaces/category")
    public ResponseEntity<?> findByCategory(@PathVariable String categoryName, @AuthenticationPrincipal Member member){
        Category findCategory = categoryService.findByName(categoryName);

        if(findCategory == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<SpaceCategory> spaceCategoryList = spaceCategoryService.findByCategoryId(findCategory.getId());

        ArrayList<SpaceDto> resultSpaceDto = new ArrayList<>();

        for(SpaceCategory spaceCategory: spaceCategoryList){
            Space space = spaceCategory.getSpace();
            Member createMember = memberService.findByUserId(space.getCreatedBy());

            SpaceDto spaceDto = new SpaceDto(space,createMember);
            resultSpaceDto.add(spaceDto);
            List<MemberSpace> bySpace = memberSpaceService.findBySpace(space);
            spaceDto.setOwnerId(bySpace.get(0).getMember().getUserId());

            Likes alreadyLike = likeService.isAlreadyLike(member, space);

            if(alreadyLike == null){
                spaceDto.setLikeType(null);
            }else{
                spaceDto.setLikeType(alreadyLike.getType());
            }
        }

        return ResponseEntity.ok(new ApiResult<>(resultSpaceDto));
    }
}
