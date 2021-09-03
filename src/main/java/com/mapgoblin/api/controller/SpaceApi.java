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

        List<SpaceDto> collect = spaceList.stream()
                .map(space -> {
                    MemberSpace memberSpace = memberSpaceService.findBySpace(space).stream().findFirst().orElse(null);
                    Member createMember = memberService.findByUserId(space.getCreatedBy());
                    SpaceDto spaceDto = new SpaceDto(space, createMember);

                    spaceDto.setOwnerId(memberSpace.getMember().getUserId());

                    spaceDto.setLikeType(getLikeTypeByMember(member, space));

                    return spaceDto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResult(collect));
    }

    @GetMapping("{userId}/spaces")
    public ResponseEntity<?> getMySpaceList(@PathVariable String userId){
        Member findMember = memberService.findByUserId(userId);

        List<MemberSpace> spacesOfMember = memberSpaceService.findSpacesOfMember(findMember);

        List<SpaceDto> collect = spacesOfMember.stream()
                .map(memberSpace -> {
                    Space space = memberSpace.getSpace();

                    SpaceDto spaceDto = new SpaceDto(space);

                    spaceDto.setLikeType(getLikeTypeByMember(findMember, space));

                    return spaceDto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResult(collect));
    }

    private LikeType getLikeTypeByMember(Member member, Space space) {
        Likes alreadyLike = likeService.isAlreadyLike(member, space);

        return alreadyLike == null ? null : alreadyLike.getType();
    }

    @GetMapping("/{userId}/spaces/{repositoryName}")
    public ResponseEntity<?> getSpace(@PathVariable String userId, @PathVariable String repositoryName, @AuthenticationPrincipal Member member){
        List<SpaceResponse> list = null;

        Member findMember = memberService.findByUserId(userId);

        list = spaceService.findOne(findMember.getId(), repositoryName);

        if(list != null && list.size() > 0){
            SpaceResponse spaceResponse = list.get(0);

            Space targetSpace = spaceService.findById(spaceResponse.getId());

            setResponseInfo(spaceResponse, targetSpace, findMember, member);

            return ResponseEntity.ok(spaceResponse);
        }else{
            return ApiResult.errorMessage("없는 지도입니다.", HttpStatus.BAD_REQUEST);
        }
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
        Space byId = spaceService.findById(spaceResponse.getHostId());
        List<MemberSpace> bySpace = memberSpaceService.findBySpace(byId);

        spaceResponse.setHostRepoName(byId.getName());

        String hostUserId = bySpace.get(0).getMember().getUserId();

        spaceResponse.setHostUserId(hostUserId);
    }

    private void setResponseAuthority(SpaceResponse spaceResponse, Member findMember, Member member) {
        if(findMember.getId().equals(member.getId())){
            spaceResponse.setAuthority("OWNER");
        }else{
            spaceResponse.setAuthority("VIEWER");
        }
    }

    @PostMapping("/{userId}/spaces/{repositoryName}")
    public ResponseEntity<?> modifyInfo(@RequestBody CreateSpaceRequest request, @PathVariable String userId, @PathVariable String repositoryName, @AuthenticationPrincipal Member member){
        List<SpaceResponse> list = null;

        list = spaceService.findOne(member.getId(), repositoryName);

        if(list != null && list.size() > 0){
            SpaceResponse spaceResponse = list.get(0);

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

        List<MemberSpace> spacesOfMember = memberSpaceService.findSpacesOfMember(member);

        CreateSpaceResponse response = null;

        List<String> spaceNames = spacesOfMember.stream()
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

        CreateSpaceResponse response;

        Space hostSpace = spaceService.findById(cloneRequest.getRepositoryId());

        if (hostSpace == null){
            return ApiResult.errorMessage("없는 지도 클론", HttpStatus.CONFLICT);
        }

        List<SpaceResponse> byMemberIdAndHostId = spaceService.findByMemberIdAndHostId(member.getId(), cloneRequest.getRepositoryId());

        if (byMemberIdAndHostId.size() > 0){
            return ApiResult.errorMessage("이미 클론 한 지도입니다.", HttpStatus.CONFLICT);
        }

        response = spaceService.clone(member.getId(), hostSpace);

        alarmService.save(cloneRequest.getRepositoryId(), AlarmType.CLONE);

        if(response == null){
            return ApiResult.errorMessage("지도 클론 에러", HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/spaces/{id}/delete")
    public ResponseEntity<?> spaceDelete(@PathVariable Long id,  @AuthenticationPrincipal Member member){
        Space findSpace = spaceService.findById(id);
        Boolean checkHost = false;

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

        return ResponseEntity.ok(checkHost);
    }

    /**
     * Get user liked spaces
     *
     * @return
     */
    @GetMapping("/{userId}/spaces/likes")
    public ResponseEntity<?> getUserLikedSpaceList(@PathVariable Long userId){
        Member findMember = memberService.findById(userId);

        List<Likes> spaceIdList = likeService.findByMemberId(userId);

        if(spaceIdList == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<SpaceDto> results = spaceIdList.stream()
                .map(like -> {
                    Space space = like.getSpace();
                    Member createMember = memberService.findByUserId(space.getCreatedBy());

                    SpaceDto spaceDto = new SpaceDto(space, createMember);

                    MemberSpace memberSpace = memberSpaceService.findBySpace(space).stream().findFirst().orElse(null);

                    spaceDto.setOwnerId(memberSpace.getMember().getUserId());

                    spaceDto.setLikeType(getLikeTypeByMember(findMember, space));

                    return spaceDto;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResult<>(results));
    }
}
