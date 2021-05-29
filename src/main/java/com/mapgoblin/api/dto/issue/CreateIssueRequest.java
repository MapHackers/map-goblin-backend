package com.mapgoblin.api.dto.issue;

import com.mapgoblin.domain.Space;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateIssueRequest {
    private String title;

    private String content;
}
