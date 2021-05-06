package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.space.CreateSpaceRequest;
import com.mapgoblin.api.dto.space.CreateSpaceResponse;
import com.mapgoblin.api.dto.space.SpaceDto;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.MemberSpace;
import com.mapgoblin.domain.Space;
import com.mapgoblin.service.MemberService;
import com.mapgoblin.service.MemberSpaceService;
import com.mapgoblin.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/repositories")
@RequiredArgsConstructor
public class SpaceApi {

    private final MemberService memberService;
    private final MemberSpaceService memberSpaceService;
    private final SpaceService spaceService;

    /**
     * Get all repositories
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<?> get() {

        List<Space> spaceList = spaceService.findAll();

        List<SpaceDto> collect = spaceList.stream()
                .map(space -> new SpaceDto(space))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResult(collect));
    }

    /**
     * Create repository
     *
     * @param request
     * @param member
     * @return
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateSpaceRequest request, @AuthenticationPrincipal Member member) {

        List<MemberSpace> spacesOfMember = memberSpaceService.findSpacesOfMember(member);

        CreateSpaceResponse response = null;

        List<String> spaceNames = spacesOfMember.stream()
                .map(memberSpace -> memberSpace.getSpace().getName())
                .collect(Collectors.toList());

        if(spaceNames.contains(request.getName())){
            return ApiResult.errorMessage("동일한 지도명이 존재합니다.", HttpStatus.CONFLICT);
        }

        try{
            response = spaceService.create(member.getId(), request);
        }catch (Exception e){
            return ApiResult.errorMessage("지도 생성 에러", HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok(response);
    }
}
