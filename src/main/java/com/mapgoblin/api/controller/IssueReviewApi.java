package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.issue.CreateIssueReviewRequest;
import com.mapgoblin.api.dto.issue.CreateIssueReviewResponse;
import com.mapgoblin.api.dto.issue.GetIssueResponse;
import com.mapgoblin.domain.Issue;
import com.mapgoblin.domain.IssueReview;
import com.mapgoblin.domain.Member;
import com.mapgoblin.service.IssueReviewService;
import com.mapgoblin.service.IssueService;
import com.mapgoblin.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class IssueReviewApi {
    private final IssueReviewService issueReviewService;
    private final IssueService issueService;
    private final MemberService memberService;

    @PostMapping("/{userId}/repositories/{repositoryName}/issues/{id}")
    public ResponseEntity<?> create(@RequestBody CreateIssueReviewRequest request, @PathVariable String userId, @PathVariable String repositoryName, @PathVariable Long id) {

        return ResponseEntity.ok(issueReviewService.save(request, id));
    }

    @GetMapping("/{userId}/repositories/{repositoryName}/issues/{id}/reviews")
    public ResponseEntity<?> getIssueReviewList(@PathVariable String userId, @PathVariable String repositoryName, @PathVariable Long id) {
        List<IssueReview> issueReviewList = issueReviewService.findByIssueId(id);
        List<CreateIssueReviewResponse> collect = issueReviewList.stream()
                .map(issueReview -> new CreateIssueReviewResponse(issueReview.getId(),issueReview.getAuthor(),issueReview.getContent(),issueReview.getCreatedDate()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResult(collect));
    }
}
