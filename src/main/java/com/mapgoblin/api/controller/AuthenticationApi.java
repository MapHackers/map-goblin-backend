package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.mail.MailDto;
import com.mapgoblin.api.dto.member.FindMemberRequest;
import com.mapgoblin.api.dto.member.FindMemberResponse;
import com.mapgoblin.api.dto.member.JwtTokenProvider;
import com.mapgoblin.domain.Member;
import com.mapgoblin.service.MailService;
import com.mapgoblin.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
public class AuthenticationApi {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final MailService mailService;

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

    @PostMapping("/email")
    public ResponseEntity<?> emailAuthentication(@RequestBody MailDto request, HttpSession session){

        if (request.getEmail() != null){

            String authNumber = getAuthNumber();

            request.setTitle("[지도깨비] 이메일 인증 메일입니다.");
            request.setContent("안녕하세요. 지도깨비입니다.\n아래의 인증번호를 화면에 입력하고 인증을 완료해주세요\n 인증번호 : " + authNumber);

            session.setAttribute(request.getEmail(), authNumber);

            mailService.send(request);
        }else{
            return ApiResult.errorMessage("이메일 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("이메일에서 인증번호를 확인해주세요.");
    }

    @PostMapping("/checkNumber")
    public ResponseEntity<?> checkAuthenticationNumber(@RequestBody MailDto request, HttpSession session){

        if (request.getCode() != null){
            if (!request.getCode().equals(session.getAttribute(request.getEmail()))){
                return ApiResult.errorMessage("잘못된 인증번호입니다.", HttpStatus.UNAUTHORIZED);
            }
        }else{
            return ApiResult.errorMessage("인증번호 정보가 없습니다.", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("인증되었습니다.");
    }

    private String getAuthNumber(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String result = "";

        int idx = 0;
        for (int i = 0; i < 6; i++) {
            idx = (int) (charSet.length * Math.random());
            result += charSet[idx];
        }
        return result;
    }
}
