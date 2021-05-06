package com.mapgoblin.repository;

import com.mapgoblin.api.dto.space.CreateSpaceResponse;
import com.mapgoblin.api.dto.space.SpaceResponse;
import com.mapgoblin.domain.MemberSpace;

import java.util.List;

public interface MemberSpaceRepositoryCustom {

    List<SpaceResponse> findByMemberIdAndSpaceName(Long memberId, String spaceName);
}
