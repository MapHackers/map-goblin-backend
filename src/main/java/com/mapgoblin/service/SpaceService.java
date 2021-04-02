package com.mapgoblin.service;

import com.mapgoblin.api.dto.space.CreateSpaceRequest;
import com.mapgoblin.api.dto.space.CreateSpaceResponse;
import com.mapgoblin.domain.Map;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.MemberSpace;
import com.mapgoblin.domain.Space;
import com.mapgoblin.repository.MapRepository;
import com.mapgoblin.repository.MemberRepository;
import com.mapgoblin.repository.MemberSpaceRepository;
import com.mapgoblin.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpaceService {

    private final MemberRepository memberRepository;
    private final SpaceRepository spaceRepository;
    private final MapRepository mapRepository;
    private final MemberSpaceRepository memberSpaceRepository;

    /**
     * Find all repositories
     *
     * @return
     */
    public List<Space> findAll() {
        return spaceRepository.findAll();
    }

    /**
     * Create Space
     *
     * @param memberId
     * @param request
     * @return
     */
    @Transactional
    public CreateSpaceResponse create(Long memberId, CreateSpaceRequest request) {

        Member member = memberRepository.findById(memberId).orElse(null);
        Map map = Map.createMap();

        mapRepository.save(map);

        Space space = Space.createSpace(request.getName(), request.getThumbnail(), request.getDescription(), map);

        MemberSpace memberSpace = MemberSpace.createMemberSpace(space);

        member.addMemberSpace(memberSpace);

        memberSpaceRepository.save(memberSpace);

        spaceRepository.save(space);

        return new CreateSpaceResponse(
                space.getId(),
                space.getMap().getId(),
                space.getName(),
                space.getThumbnail(),
                space.getDescription(),
                space.getLikeCount(),
                space.getDislikeCount());
    }
}
