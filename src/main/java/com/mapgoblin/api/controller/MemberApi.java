package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.member.*;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.base.MemberRole;
import com.mapgoblin.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberApi {

    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;

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
    public ResponseEntity<?> modifyPassword(@PathVariable Long id, @RequestBody CreateMemberRequest request) {

        Member member = memberService.modifyPassword(id, passwordEncoder.encode(request.getPassword()));

        if (member == null) {
            return ApiResult.errorMessage("비밀번호 변경 실패", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
