package com.mapgoblin.repository;

import com.mapgoblin.domain.Issue;
import com.mapgoblin.domain.Space;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    Optional<List<Issue>> findBySpace(Space space);

}
