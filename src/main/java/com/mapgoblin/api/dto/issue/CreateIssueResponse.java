package com.mapgoblin.api.dto.issue;

import com.mapgoblin.domain.base.IssueStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateIssueResponse {
    private String title;

    private String content;

    private IssueStatus status;

    private LocalDateTime createdDate;

    private String createdBy;
}
