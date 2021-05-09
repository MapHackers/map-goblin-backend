package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.space.*;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.MemberSpace;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.SourceType;
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
    @GetMapping("/repositories")
    public ResponseEntity<?> getRepositoryList() {

        List<Space> spaceList = spaceService.findAll();

        List<SpaceDto> collect = spaceList.stream()
                .map(space -> new SpaceDto(space))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResult(collect));
    }

    @GetMapping("/{userId}/repositories/{repositoryName}")
    public ResponseEntity<?> getRepository(@PathVariable String userId, @PathVariable String repositoryName, @AuthenticationPrincipal Member member){
        List<SpaceResponse> list = null;

        try{
            Member findMember = memberService.findByUserId(userId);

            list = spaceService.findOne(findMember.getId(), repositoryName);

            if(list != null && list.size() > 0){
                SpaceResponse spaceResponse = list.get(0);

                if(findMember.getId().equals(member.getId())){
                    spaceResponse.setSource(SourceType.HOST);
                }else{
                    spaceResponse.setSource(SourceType.CLONE);
                }

                return ResponseEntity.ok(spaceResponse);
            }else{
                return ApiResult.errorMessage("없는 지도입니다.", HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();

            return ApiResult.errorMessage("조회 에러", HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Create repository
     *
     * @param request
     * @param member
     * @return
     */
    @PostMapping("/repositories")
    public ResponseEntity<?> create(@RequestBody CreateSpaceRequest request, @AuthenticationPrincipal Member member) {

        List<MemberSpace> spacesOfMember = memberSpaceService.findSpacesOfMember(member);

        CreateSpaceResponse response = null;

        System.out.println("////////////////////////////////////////");
        System.out.println(request.getThumbnail());
        System.out.println("////////////////////////////////////////");

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

    /**
     * Clone repository
     *
     * @param cloneRequest
     * @param member
     * @return
     */
    @PostMapping("/repositories/clone")
    public ResponseEntity<?> repositoryClone(@RequestBody CloneRequest cloneRequest, @AuthenticationPrincipal Member member){

        CreateSpaceResponse response = null;

        try{
            Space hostSpace = spaceService.findById(cloneRequest.getRepositoryId());
            if (hostSpace == null){
                return ApiResult.errorMessage("없는 지도 클론", HttpStatus.CONFLICT);
            }
            response = spaceService.clone(member.getId(), hostSpace);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResult.errorMessage("지도 클론 에러", HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok(response);
    }
}
