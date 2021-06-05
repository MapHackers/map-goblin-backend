package com.mapgoblin.api.dto.issue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateIssueReviewResponse {
    private Long id;
    private String author;
    private String content;
    private LocalDateTime createdDate;
}
