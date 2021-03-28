package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.member.*;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.base.MemberRole;
import com.mapgoblin.exception.WrongPasswordException;
import com.mapgoblin.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApi {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    /**
     * Sign Up
     *
     * @param request
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody CreateMemberRequest request) {

        Member member = new Member(
                request.getUserId(),
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                MemberRole.ROLE_USER);

        CreateMemberResponse response = memberService.save(member);

        return ResponseEntity.ok(response);
    }

    /**
     * Sign In
     *
     * @param request
     * @return
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody FindMemberRequest request) {

        Member member = memberService.findByUserId(request.getUserId());

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new WrongPasswordException("잘못된 비밀번호입니다.");
        }

        FindMemberResponse response = new FindMemberResponse(
                member.getId(),
                member.getUserId(),
                member.getName(),
                member.getEmail(),
                jwtTokenProvider.createToken(member.getEmail(), member.getRole()));

        return ResponseEntity.ok(response);
    }
}
