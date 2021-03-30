package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.member.FindMemberRequest;
import com.mapgoblin.api.dto.member.FindMemberResponse;
import com.mapgoblin.api.dto.member.JwtTokenProvider;
import com.mapgoblin.domain.Member;
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
public class LoginApi {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    /**
     * Login
     *
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody FindMemberRequest request) {

        Member member = memberService.findByUserId(request.getUserId());

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new WrongPasswordException("잘못된 비밀번호입니다.");
        }

        FindMemberResponse response = new FindMemberResponse(
                member.getId(),
                member.getUserId(),
                member.getName(),
                member.getEmail(),
                jwtTokenProvider.createToken(member.getUserId(), member.getRole()));

        return ResponseEntity.ok(response);
    }
}
