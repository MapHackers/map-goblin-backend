package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.member.*;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.base.MemberRole;
import com.mapgoblin.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        Member member = new Member(
                request.getUserId(),
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                MemberRole.ROLE_USER);

        CreateMemberResponse response = memberService.save(member);

        return ResponseEntity.ok(response);
    }
}
