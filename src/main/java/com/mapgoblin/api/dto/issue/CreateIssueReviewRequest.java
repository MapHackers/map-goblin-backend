package com.mapgoblin.api.dto.issue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateIssueReviewRequest {
    private String author;

    private String content;

    private String profile;
}
