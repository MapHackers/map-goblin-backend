package com.mapgoblin.service;

import com.mapgoblin.api.dto.member.CreateMemberResponse;
import com.mapgoblin.api.dto.member.EditProfileResponse;
import com.mapgoblin.domain.Member;
import com.mapgoblin.exception.UserNotFoundException;
import com.mapgoblin.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public EditProfileResponse editNameAndDescription(String userId, String userName, String description, String profile){
        Member member = memberRepository.findByUserId(userId).orElse(null);
        if (member != null){
            member.setName(userName);
            member.setDescription(description);
            if(profile != null){
                if(profile.equals("profileDelete")){
                    member.setProfile(null);
                }else{
                    member.setProfile(profile);
                }
            }
            return new EditProfileResponse(member.getUserId(),member.getName(), member.getDescription(), member.getProfile());
        }
        else{
            return null;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return memberRepository.findByUserId(username)
//                .orElseThrow(() -> new UserNotFoundException("가입되지 않은 아이디 입니다."));

        return memberRepository.findByUserId(username)
                .orElse(null);
    }

    public List<Member> search(String keyword){

        return memberRepository.findByNameContaining(keyword).orElse(null);
    }
}
