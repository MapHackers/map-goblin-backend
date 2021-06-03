package com.mapgoblin.api.dto.issue;

import com.mapgoblin.domain.Issue;
import com.mapgoblin.domain.base.IssueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetIssueResponse {
    private String title;

    private String content;

    private IssueStatus status;

    private LocalDateTime createdDate;

    private String createdBy;

    public GetIssueResponse(Issue issue) {
        this.title = issue.getTitle();
        this.content = issue.getContent();
        this.status = issue.getStatus();
        this.createdBy = issue.getCreatedBy();
        this.createdDate = issue.getCreatedDate();
    }
}
