package com.mapgoblin.repository;

import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.MemberSpace;
import com.mapgoblin.domain.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberSpaceRepository extends JpaRepository<MemberSpace, Long>, MemberSpaceRepositoryCustom {

    Optional<List<MemberSpace>> findByMember(Member member);

    Optional<List<MemberSpace>> findBySpace(Space space);
}
