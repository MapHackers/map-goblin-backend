package com.mapgoblin.service;

import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.MemberSpace;
import com.mapgoblin.domain.Space;
import com.mapgoblin.repository.MemberSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberSpaceService {

    private final MemberSpaceRepository memberSpaceRepository;

    public List<MemberSpace> findAll(){
        return memberSpaceRepository.findAll();
    }

    public List<MemberSpace> findSpacesOfMember(Member member){
        return memberSpaceRepository.findByMemberOrderByCreatedDateDesc(member).orElse(null);
    }

    public List<MemberSpace> findBySpace(Space space){
        return memberSpaceRepository.findBySpace(space).orElse(null);
    }

}
