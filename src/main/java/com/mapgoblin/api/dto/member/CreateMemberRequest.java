package com.mapgoblin.api.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateMemberRequest {

    private String userId;

    private String name;

    private String email;

    private String password;
}
