package com.mapgoblin.service;

import com.mapgoblin.api.dto.issue.CreateIssueReviewRequest;
import com.mapgoblin.api.dto.issue.CreateIssueReviewResponse;
import com.mapgoblin.domain.Issue;
import com.mapgoblin.domain.IssueReview;
import com.mapgoblin.repository.IssueRepository;
import com.mapgoblin.repository.IssueReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IssueReviewService {
    private final IssueReviewRepository issueReviewRepository;
    private final IssueRepository issueRepository;

    @Transactional
    public CreateIssueReviewResponse save(CreateIssueReviewRequest request, Long id){
        Issue issue = issueRepository.findById(id).orElse(null);
        assert issue != null;
        IssueReview issueReview = IssueReview.create(issue, request.getAuthor(), request.getContent(), request.getProfile());
        issueReviewRepository.save(issueReview);
        issue.addIssueReview(issueReview);
        return new CreateIssueReviewResponse(issueReview.getId(), issueReview.getAuthor(), issueReview.getContent(), issueReview.getProfile(), issueReview.getCreatedDate());
    }

    public List<IssueReview> findByIssueId(Long id) {
        Issue issue = issueRepository.findById(id).orElse(null);
        assert issue != null;
        return issue.getIssueReviewList();
    }
}
