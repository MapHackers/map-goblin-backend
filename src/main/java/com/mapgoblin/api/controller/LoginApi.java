package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.member.FindMemberRequest;
import com.mapgoblin.api.dto.member.FindMemberResponse;
import com.mapgoblin.api.dto.member.JwtTokenProvider;
import com.mapgoblin.domain.Member;
import com.mapgoblin.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

        if (member == null || !passwordEncoder.matches(request.getPassword(), member.getPassword())) {
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
                        jwtTokenProvider.createToken(findMember.getUserId(), findMember.getRole()));

                return ResponseEntity.ok(response);
            }
        }
    }

    /**
     * User authentication
     *
     * @param headers
     * @return
     */
    @GetMapping("/authentication")
    public ResponseEntity<?> authentication(@RequestHeader HttpHeaders headers) {
        String token = null;

        try{
            token = headers.get("X-AUTH-TOKEN").get(0);
        }catch (Exception e){
            return ApiResult.errorMessage("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        if (token != null && jwtTokenProvider.validateToken(token)){
            String userId = jwtTokenProvider.getUserPk(token);

            Member member = memberService.findByUserId(userId);

            FindMemberResponse response = new FindMemberResponse(
                    member.getId(),
                    member.getUserId(),
                    member.getName(),
                    member.getEmail(),
                    token);

            return ResponseEntity.ok(response);

        }else{
            return ApiResult.errorMessage("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }
    }
}
