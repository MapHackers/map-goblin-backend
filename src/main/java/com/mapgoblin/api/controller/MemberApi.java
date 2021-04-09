package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.member.*;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.base.MemberRole;
import com.mapgoblin.exception.UserNotFoundException;
import com.mapgoblin.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

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
        //Member findMember = null;

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
                return getConflictResponseEntity("이미 존재하는 아이디입니다.");
            }
        }else{
            return getConflictResponseEntity("해당 이메일로 가입된 아이디가 존재합니다.");
        }

        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> getConflictResponseEntity(String s) {
        HashMap<String, String> result = new HashMap<String, String>();

        result.put("message", s);

        return new ResponseEntity<>(result, HttpStatus.CONFLICT);
    }
}
