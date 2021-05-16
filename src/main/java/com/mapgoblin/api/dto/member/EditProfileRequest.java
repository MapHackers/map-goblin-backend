package com.mapgoblin.api.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileRequest {

    private String userId;

    private String userName;

    private String description;

    private String profile;
}
