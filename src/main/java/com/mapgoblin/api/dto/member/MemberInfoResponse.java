package com.mapgoblin.api.dto.member;

import com.mapgoblin.api.dto.space.SpaceDto;
import com.mapgoblin.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
public class MemberInfoResponse {

    private String userId;

    private String name;

    private String email;

    private String description;

    private String profile;

    private List<SpaceDto> mapList;

    public MemberInfoResponse(Member member, List<SpaceDto> mapList){
        this.userId = member.getUserId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.description = member.getDescription();
        this.profile = member.getProfile();
        this.mapList = mapList;
    }
}
