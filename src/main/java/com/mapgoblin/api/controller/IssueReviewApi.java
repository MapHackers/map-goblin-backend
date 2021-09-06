package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.issue.CreateIssueReviewRequest;
import com.mapgoblin.api.dto.issue.CreateIssueReviewResponse;
import com.mapgoblin.domain.Member;
import com.mapgoblin.service.IssueReviewService;
import com.mapgoblin.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class IssueReviewApi {
    private final IssueReviewService issueReviewService;
    private final MemberService memberService;

    @PostMapping("/{userId}/spaces/{spaceName}/issues/{id}")
    public ResponseEntity<?> create(@RequestBody CreateIssueReviewRequest request, @PathVariable String userId, @PathVariable String spaceName, @PathVariable Long id) {

        Member findMember = memberService.findByUserId(userId);

        CreateIssueReviewResponse createIssueReviewResponse = issueReviewService.save(request, id);

        createIssueReviewResponse.setAuthor(findMember.getName());
        createIssueReviewResponse.setProfile(findMember.getProfile());

        return ResponseEntity.ok(createIssueReviewResponse);
    }
}
