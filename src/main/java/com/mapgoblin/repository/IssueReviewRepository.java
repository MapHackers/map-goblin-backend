package com.mapgoblin.repository;

import com.mapgoblin.domain.Issue;
import com.mapgoblin.domain.IssueReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueReviewRepository  extends JpaRepository<IssueReview, Long> {

    Optional<List<IssueReview>> findByIssue(Issue issue);
}
