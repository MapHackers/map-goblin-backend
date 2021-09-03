package com.mapgoblin.repository;

import com.mapgoblin.api.dto.space.SpaceResponse;

import java.util.List;

public interface MemberSpaceRepositoryCustom {

    List<SpaceResponse> findByMemberIdAndSpaceName(Long memberId, String spaceName);

    List<SpaceResponse> findByMemberIdAndHostId(Long memberId, Long hostId);
}
