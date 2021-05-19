package com.mapgoblin.repository;

import com.mapgoblin.domain.Alarm;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    Optional<List<Alarm>> findByDstMemberOrderByCreatedDateDesc(Member member);

    Optional<List<Alarm>> findByDstSpace(Space space);
}
