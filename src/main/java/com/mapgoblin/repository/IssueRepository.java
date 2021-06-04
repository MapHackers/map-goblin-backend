package com.mapgoblin.repository;

import com.mapgoblin.domain.Issue;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.IssueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    Page<Issue> findBySpaceAndStatus(Space space, IssueStatus status, Pageable pageable);

    Optional<List<Issue>> findBySpace(Space space);

}
