package com.mapgoblin.service;

import com.mapgoblin.api.dto.member.CreateMemberResponse;
import com.mapgoblin.domain.Member;
import com.mapgoblin.exception.UserNotFoundException;
import com.mapgoblin.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * Find member information with pk
     *
     * @param memberId
     * @return
     */
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElse(null);
    }

    /**
     * Find member information with userId
     *
     * @param userId
     * @return
     */
    public Member findByUserId(String userId) {

//        return memberRepository.findByUserId(userId)
//                .orElseThrow(() -> new UserNotFoundException("가입되지 않은 아이디 입니다."));
        return memberRepository.findByUserId(userId)
                .orElse(null);
    }

    /**
     * Find member information with email
     *
     * @param email
     * @return
     */
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElse(null);
    }

    /**
     * Save member information
     *
     * @param member
     * @return
     */
    @Transactional
    public CreateMemberResponse save(Member member) {

        memberRepository.save(member);
        System.out.println("===================================================");
        System.out.println(member.getDescription());

        return new CreateMemberResponse(member.getId(), member.getUserId(), member.getName(), member.getEmail(), member.getDescription());
    }

    /**
     * Modify user password
     *
     * @param id
     * @param password
     * @return
     */
    @Transactional
    public Member modifyPassword(Long id, String password) {
        Member member = memberRepository.findById(id)
                .orElse(null);

        if (member != null){
            member.setPassword(password);
        }

        return member;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return memberRepository.findByUserId(username)
//                .orElseThrow(() -> new UserNotFoundException("가입되지 않은 아이디 입니다."));

        return memberRepository.findByUserId(username)
                .orElse(null);
    }
}
