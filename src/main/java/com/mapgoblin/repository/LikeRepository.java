package com.mapgoblin.repository;

import com.mapgoblin.domain.Likes;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByMemberAndSpace(Member member, Space space);
}
