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

@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    /**
     * Save member information
     *
     * @param member
     * @return
     */
    public CreateMemberResponse save(Member member) {

        memberRepository.save(member);

        return new CreateMemberResponse(member.getId(), member.getUserId(), member.getName(), member.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
