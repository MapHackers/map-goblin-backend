package com.mapgoblin.api.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindMemberRequest {

    private String userId;

    private String password;
}
