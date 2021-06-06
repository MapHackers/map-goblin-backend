package com.mapgoblin.api.dto.issue;

import com.mapgoblin.domain.Issue;
import com.mapgoblin.domain.IssueReview;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.base.IssueStatus;
import com.mapgoblin.domain.base.IssueTag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetIssueResponse {

    private Long id;

    private String title;

    private String content;

    private IssueStatus status;

    private IssueTag tag;

    private LocalDateTime createdDate;

    private String createdBy;

    private String authorName;

    private String authorProfile;

    private List<CreateIssueReviewResponse> issueReviewList = new ArrayList<>();

    public GetIssueResponse(Issue issue) {
        this.id = issue.getId();
        this.title = issue.getTitle();
        this.content = issue.getContent();
        this.status = issue.getStatus();
        this.tag = issue.getTag();
        this.createdBy = issue.getCreatedBy();
        this.createdDate = issue.getCreatedDate();
//        this.issueReviewList = issue.getIssueReviewList().stream()
//                .map(review -> new CreateIssueReviewResponse(review.getId(), review.getAuthor(), review.getContent(), review.getProfile(), review.getCreatedDate()))
//                .collect(Collectors.toList());
    }

    public GetIssueResponse(Issue issue, Member member) {
        this.id = issue.getId();
        this.title = issue.getTitle();
        this.content = issue.getContent();
        this.status = issue.getStatus();
        this.tag = issue.getTag();
        this.createdBy = issue.getCreatedBy();
        this.createdDate = issue.getCreatedDate();
        this.authorName = member.getName();
        this.authorProfile = member.getProfile();
//        this.issueReviewList = issue.getIssueReviewList().stream()
//                .map(review -> new CreateIssueReviewResponse(review.getId(), review.getAuthor(), review.getContent(), review.getProfile(), review.getCreatedDate()))
//                .collect(Collectors.toList());
    }
}
