package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.member.CreateMemberResponse;
import com.mapgoblin.api.dto.space.SpaceDto;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.Space;
import com.mapgoblin.service.MemberService;
import com.mapgoblin.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchApi {

    private final MemberService memberService;
    private final SpaceService spaceService;

    @GetMapping("/repositories/{keyword}")
    public ResponseEntity<?> getSearchedRepositoryList(@PathVariable String keyword){
        List<Space> spaceList = spaceService.search(keyword);

        List<SpaceDto> collect = spaceList.stream()
                .map(space -> new SpaceDto(space, space.getCreatedBy()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResult(collect));
    }

    @GetMapping("/members/{keyword}")
    public ResponseEntity<?> getSearchedMemberList(@PathVariable String keyword){
        List<Member> members = memberService.search(keyword);

        List<CreateMemberResponse> collect = members.stream()
                .map(member -> new CreateMemberResponse(member.getId(),member.getUserId(), member.getName(),member.getEmail(), member.getDescription()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResult(collect));
    }
}
