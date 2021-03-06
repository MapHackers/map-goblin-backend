package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.member.*;
import com.mapgoblin.api.dto.space.SpaceDto;
import com.mapgoblin.domain.Likes;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.MemberSpace;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.MemberRole;
import com.mapgoblin.service.LikeService;
import com.mapgoblin.service.MemberService;
import com.mapgoblin.service.MemberSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberApi {

    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final MemberSpaceService memberSpaceService;
    private final LikeService likeService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getMemberInfo(@PathVariable String userId) {

        MemberInfoResponse response = null;

        Member findMember = memberService.findByUserId(userId);
        if (findMember == null) {
            return ApiResult.errorMessage("존재하지 않는 유저입니다.", HttpStatus.NOT_FOUND);
        }

        List<MemberSpace> spacesOfMember = memberSpaceService.findSpacesOfMember(findMember);

        List<SpaceDto> mapList = spacesOfMember.stream()
                .map(memberSpace -> {
                    Space space = memberSpace.getSpace();
                    Member createMember = memberService.findByUserId(space.getCreatedBy());
                    SpaceDto spaceDto = new SpaceDto(memberSpace.getSpace(), createMember);

                    Likes alreadyLike = likeService.isAlreadyLike(findMember, space);

                    if(alreadyLike == null){
                        spaceDto.setLikeType(null);
                    }else{
                        spaceDto.setLikeType(alreadyLike.getType());
                    }

                    return spaceDto;
                })
                .collect(Collectors.toList());

        response = new MemberInfoResponse(findMember, mapList);

        return ResponseEntity.ok(response);
    }

    /**
     * Sign Up
     *
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateMemberRequest request) {

        CreateMemberResponse response = null;

        Member findMember = memberService.findByEmail(request.getEmail());

        if (findMember == null) {
            findMember = memberService.findByUserId(request.getUserId());

            if (findMember == null) {
                Member member = Member.createMember(
                        request.getUserId(),
                        request.getName(),
                        request.getEmail(),
                        passwordEncoder.encode(request.getPassword()),
                        MemberRole.ROLE_USER);

                response = memberService.save(member);
            }else{
                return ApiResult.errorMessage("이미 존재하는 아이디입니다.", HttpStatus.CONFLICT);
            }
        }else{
            return ApiResult.errorMessage("해당 이메일로 가입된 아이디가 존재합니다.", HttpStatus.CONFLICT);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/password")
    public ResponseEntity<?> modifyPassword(@PathVariable Long id, @RequestBody CreateMemberRequest request, @AuthenticationPrincipal Member member) {

        if (member.getId() != id) {
            return ApiResult.errorMessage("잘못된 접근입니다.", HttpStatus.BAD_REQUEST);
        }

        Member findMember = memberService.modifyPassword(id, passwordEncoder.encode(request.getPassword()));

        if (findMember == null) {
            return ApiResult.errorMessage("비밀번호 변경 실패", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/profile")
    public ResponseEntity<?> editProfile(@RequestBody EditProfileRequest request){

        EditProfileResponse response = null;

        response = memberService.editNameAndDescription(request.getUserId(),request.getUserName(),request.getDescription(),request.getProfile());

        if(response == null){
            return ApiResult.errorMessage("프로필 변경 에러", HttpStatus.BAD_REQUEST);
        }
        else{
            return ResponseEntity.ok(response);
        }
    }
}
