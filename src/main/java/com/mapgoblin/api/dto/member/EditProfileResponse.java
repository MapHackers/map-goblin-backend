package com.mapgoblin.api.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditProfileResponse {

    private String userId;

    private String userName;

    private String description;

    private String profile;
}
