package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.AlarmType;
import com.mapgoblin.domain.base.LikeType;
import com.mapgoblin.service.AlarmService;
import com.mapgoblin.service.LikeService;
import com.mapgoblin.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class LikeApi {
    private final LikeService likeService;
    private final SpaceService spaceService;
    private final AlarmService alarmService;

    @PostMapping("/{spaceId}/like")
    public ResponseEntity<?> addLike(@RequestBody HashMap<String, String> request, @AuthenticationPrincipal Member member, @PathVariable Long spaceId){

        Space space = spaceService.findById(spaceId);
        int result = 0;

        if(space == null){
            return ApiResult.errorMessage("없는 지도입니다.", HttpStatus.BAD_REQUEST);
        }

        if (request.get("type").equals("LIKE")){
            result = likeService.addLike(member.getId(), spaceId, LikeType.LIKE);

            if(result == 1 || result == 3){
                alarmService.save(space.getId(), AlarmType.LIKE);
            }

        }else{
            result = likeService.addLike(member.getId(), spaceId, LikeType.DISLIKE);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{userId}/{spaceName}/visit")
    public ResponseEntity<?> addVisit(@PathVariable String userId,@PathVariable String spaceName) {
        if(!likeService.addVisit(userId, spaceName)) return ApiResult.errorMessage("해당 지도가 없습니다.", HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>(HttpStatus.OK);
    }
}
