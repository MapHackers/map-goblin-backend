package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.space.*;
import com.mapgoblin.domain.*;
import com.mapgoblin.domain.base.AlarmType;
import com.mapgoblin.domain.base.LikeType;
import com.mapgoblin.service.*;
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
    private final AlarmService alarmService;
    private final LikeService likeService;

    /**
     * Get all spaces
     *
     * @return
     */
    @GetMapping("/spaces")
    public ResponseEntity<?> getSpaceList(@AuthenticationPrincipal Member member) {

        List<Space> spaceList = spaceService.findAll();

        List<SpaceDto> results = spaceList.stream()
                .map(space -> {
                    Member createMember = memberService.findByUserId(space.getCreatedBy());

                    SpaceDto spaceDto = new SpaceDto(space, createMember);
                    spaceDto.setLikeType(getLikeTypeByMember(member, space));

                    return spaceDto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResult(results));
    }

    @GetMapping("{userId}/spaces")
    public ResponseEntity<?> getMySpaceList(@PathVariable String userId){
        Member spaceMember = memberService.findByUserId(userId);

        List<MemberSpace> memberSpaces = memberSpaceService.findSpacesOfMember(spaceMember);

        List<SpaceDto> results = memberSpaces.stream()
                .map(memberSpace -> {
                    Space space = memberSpace.getSpace();

                    SpaceDto spaceDto = new SpaceDto(space, memberSpace.getMember());
                    spaceDto.setLikeType(getLikeTypeByMember(spaceMember, space));

                    return spaceDto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResult(results));
    }

    @GetMapping("/{userId}/spaces/{spaceName}")
    public ResponseEntity<?> getSpace(@PathVariable String userId, @PathVariable String spaceName, @AuthenticationPrincipal Member member){
        List<SpaceResponse> list;

        Member spaceMember = memberService.findByUserId(userId);

        list = spaceService.findOne(spaceMember.getId(), spaceName);

        if(list != null && list.size() > 0){
            SpaceResponse spaceResponse = list.get(0);

            Space targetSpace = spaceService.findById(spaceResponse.getId());

            setResponseInfo(spaceResponse, targetSpace, spaceMember, member);

            return ResponseEntity.ok(spaceResponse);
        }else{
            return ApiResult.errorMessage("없는 지도입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{userId}/spaces/{spaceName}")
    public ResponseEntity<?> modifyInfo(@RequestBody CreateSpaceRequest request, @PathVariable String userId, @PathVariable String spaceName, @AuthenticationPrincipal Member member){
        SpaceResponse spaceResponse =
                spaceService.findOne(member.getId(), spaceName)
                        .stream()
                        .findFirst()
                        .orElse(null);

        if(spaceResponse != null){
            spaceService.modify(spaceResponse.getId(), request);

            return ResponseEntity.ok("ok");
        }else{
            return ApiResult.errorMessage("없는 지도입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Create repository
     *
     * @param request
     * @param member
     * @return
     */
    @PostMapping("/spaces")
    public ResponseEntity<?> create(@RequestBody CreateSpaceRequest request, @AuthenticationPrincipal Member member) {

        List<MemberSpace> memberSpaces = memberSpaceService.findSpacesOfMember(member);

        CreateSpaceResponse response;

        List<String> spaceNames = memberSpaces.stream()
                .map(memberSpace -> memberSpace.getSpace().getName())
                .collect(Collectors.toList());

        if(spaceNames.contains(request.getName())){
            return ApiResult.errorMessage("동일한 지도명이 존재합니다.", HttpStatus.CONFLICT);
        }

        response = spaceService.create(member.getId(), request);

        return ResponseEntity.ok(response);
    }

    /**
     * Clone repository
     *
     * @param cloneRequest
     * @param member
     * @return
     */
    @PostMapping("/spaces/clone")
    public ResponseEntity<?> spaceClone(@RequestBody CloneRequest cloneRequest, @AuthenticationPrincipal Member member){

        Space hostSpace = spaceService.findById(cloneRequest.getRepositoryId());

        if (hostSpace == null){
            return ApiResult.errorMessage("없는 지도 클론", HttpStatus.CONFLICT);
        }

        List<SpaceResponse> SpaceResponses = spaceService.findByMemberIdAndHostId(member.getId(), cloneRequest.getRepositoryId());

        if (SpaceResponses.size() > 0){
            return ApiResult.errorMessage("이미 클론 한 지도입니다.", HttpStatus.CONFLICT);
        }

        CreateSpaceResponse result = spaceService.clone(member.getId(), hostSpace);

        alarmService.save(cloneRequest.getRepositoryId(), AlarmType.CLONE);

        if(result == null){
            return ApiResult.errorMessage("지도 클론 에러", HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping("/spaces/{id}/delete")
    public ResponseEntity<?> spaceDelete(@PathVariable Long id,  @AuthenticationPrincipal Member member){
        Space findSpace = spaceService.findById(id);
        boolean checkHost = false;

        if (findSpace == null) {
            return ApiResult.errorMessage("잘못된 접근", HttpStatus.BAD_REQUEST);
        }

        List<MemberSpace> findMemberSpace = memberSpaceService.findBySpace(findSpace);

        for (MemberSpace memberSpace : findMemberSpace) {
            if(memberSpace.getMember().getId().equals(member.getId())){
                checkHost = true;

                break;
            }
        }

        if(!checkHost){
            return ApiResult.errorMessage("잘못된 접근", HttpStatus.BAD_REQUEST);
        }

        spaceService.delete(findSpace);

        return ResponseEntity.ok(true);
    }

    /**
     * Get user liked spaces
     *
     * @return
     */
    @GetMapping("/{userId}/spaces/likes")
    public ResponseEntity<?> getUserLikedSpaceList(@PathVariable Long userId){
        Member member = memberService.findById(userId);

        List<Likes> likes = likeService.findByMemberId(userId);

        if(likes == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<SpaceDto> results = likes.stream()
                .map(like -> {
                    Space space = like.getSpace();
                    Member createMember = memberService.findByUserId(space.getCreatedBy());

                    SpaceDto spaceDto = new SpaceDto(space, createMember);

                    MemberSpace memberSpace = memberSpaceService.findBySpace(space).stream().findFirst().orElse(null);

                    spaceDto.setOwnerId(memberSpace.getMember().getUserId());

                    spaceDto.setLikeType(getLikeTypeByMember(member, space));

                    return spaceDto;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResult<>(results));
    }

    private LikeType getLikeTypeByMember(Member member, Space space) {
        Likes alreadyLike = likeService.isAlreadyLike(member, space);

        return alreadyLike == null ? null : alreadyLike.getType();
    }

    private void setResponseInfo(SpaceResponse spaceResponse, Space targetSpace, Member findMember, Member member) {
        setResponseCategories(spaceResponse, targetSpace);

        setResponseOwners(spaceResponse, targetSpace);

        setResponseLike(spaceResponse, targetSpace, member);

        setResponseHost(spaceResponse);

        setResponseAuthority(spaceResponse, findMember, member);
    }

    private void setResponseCategories(SpaceResponse spaceResponse, Space targetSpace) {
        List<String> categories = targetSpace.getCategories().stream()
                .map(spaceCategory -> spaceCategory.getCategory().getName())
                .collect(Collectors.toList());

        spaceResponse.setCategories(categories);
    }

    private void setResponseOwners(SpaceResponse spaceResponse, Space targetSpace) {
        List<MemberSpace> findMembers = memberSpaceService.findBySpace(targetSpace);

        List<String> owners = findMembers.stream()
                .map(memberSpace -> memberSpace.getMember().getUserId())
                .collect(Collectors.toList());

        spaceResponse.setOwners(owners);
    }

    private void setResponseLike(SpaceResponse spaceResponse, Space targetSpace, Member member) {
        spaceResponse.setLikeType(getLikeTypeByMember(member, targetSpace));
    }

    private void setResponseHost(SpaceResponse spaceResponse) {
        if(spaceResponse.getHostId() != null) {
            Space byId = spaceService.findById(spaceResponse.getHostId());
            List<MemberSpace> bySpace = memberSpaceService.findBySpace(byId);

            spaceResponse.setHostRepoName(byId.getName());

            String hostUserId = bySpace.get(0).getMember().getUserId();

            spaceResponse.setHostUserId(hostUserId);
        }
    }

    private void setResponseAuthority(SpaceResponse spaceResponse, Member findMember, Member member) {
        if(findMember.getId().equals(member.getId())){
            spaceResponse.setAuthority("OWNER");
        }else{
            spaceResponse.setAuthority("VIEWER");
        }
    }
}
