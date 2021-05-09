package com.mapgoblin.repository.impl;

import com.mapgoblin.api.dto.space.QSpaceResponse;
import com.mapgoblin.api.dto.space.SpaceResponse;
import com.mapgoblin.repository.MemberSpaceRepositoryCustom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.util.List;

import static com.mapgoblin.domain.QMemberSpace.memberSpace;
import static com.mapgoblin.domain.QSpace.space;
import static com.mapgoblin.domain.QMember.member;
import static org.springframework.util.ObjectUtils.isEmpty;

public class MemberSpaceRepositoryImpl implements MemberSpaceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberSpaceRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<SpaceResponse> findByMemberIdAndSpaceName(Long memberId, String spaceName) {

        return queryFactory
                .select(new QSpaceResponse(
                        space.id,
                        space.map.id,
                        space.name,
                        space.thumbnail,
                        space.description,
                        space.likeCount,
                        space.dislikeCount,
                        memberSpace.source,
                        space.host.id))
                .from(memberSpace)
                .leftJoin(memberSpace.space, space)
                .where(memberIdEq(memberId),
                        spaceNameEq(spaceName))
                .fetch();
    }

    @Override
    public List<SpaceResponse> findByMemberIdAndHostId(Long memberId, Long hostId) {
        return queryFactory
                .select(new QSpaceResponse(
                        space.id,
                        space.map.id,
                        space.name,
                        space.thumbnail,
                        space.description,
                        space.likeCount,
                        space.dislikeCount,
                        memberSpace.source,
                        space.host.id))
                .from(memberSpace)
                .leftJoin(memberSpace.space, space)
                .where(memberIdEq(memberId),
                        hostIdEq(hostId))
                .fetch();
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return isEmpty(memberId) ? null : memberSpace.member.id.eq(memberId);
    }

    private BooleanExpression spaceNameEq(String spaceName) {
        return isEmpty(spaceName) ? null : space.name.eq(spaceName);
    }

    private BooleanExpression hostIdEq(Long hostId) {
        return isEmpty(hostId) ? null : space.host.id.eq(hostId);
    }
}
