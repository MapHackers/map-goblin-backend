package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.member.FindMemberRequest;
import com.mapgoblin.api.dto.member.FindMemberResponse;
import com.mapgoblin.api.dto.member.JwtTokenProvider;
import com.mapgoblin.domain.Member;
import com.mapgoblin.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
            return ApiResult.errorMessage("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        FindMemberResponse response = new FindMemberResponse(
                member.getId(),
                member.getUserId(),
                member.getName(),
                member.getEmail(),
                jwtTokenProvider.createToken(member.getUserId(), member.getRole()));

        return ResponseEntity.ok(response);
    }

    /**
     * Find user id, password
     *
     * @param request
     * @return
     */
    @PostMapping("/find")
    public ResponseEntity<?> findId(@RequestBody FindMemberRequest request) {

        Member findMember = memberService.findByEmail(request.getEmail());

        if (request.getUserId() == null) {
            if (findMember == null){
                return ApiResult.errorMessage("이메일로 가입된 아이디가 없습니다.", HttpStatus.UNAUTHORIZED);
            }else{
                return ResponseEntity.ok(new ApiResult(findMember.getUserId()));
            }
        }else{
            if (findMember == null || !findMember.getUserId().equals(request.getUserId())){
                return ApiResult.errorMessage("이메일과 아이디가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
            }else{
                FindMemberResponse response = new FindMemberResponse(
                        findMember.getId(),
                        findMember.getUserId(),
                        findMember.getName(),
                        findMember.getEmail(),
                        null);

                return ResponseEntity.ok(response);
            }
        }
    }
}
