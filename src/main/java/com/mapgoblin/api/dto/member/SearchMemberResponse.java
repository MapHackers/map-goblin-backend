package com.mapgoblin.api.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchMemberResponse {
    private Long id;

    private String userId;

    private String name;

    private String email;

    private String description;

    private String profile;

    private int likeCounts;

    private int visitCounts;
}
