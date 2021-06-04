package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.member.SearchMemberResponse;
import com.mapgoblin.api.dto.space.SpaceDto;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.MemberSpace;
import com.mapgoblin.domain.Space;
import com.mapgoblin.service.MemberService;
import com.mapgoblin.service.MemberSpaceService;
import com.mapgoblin.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchApi {

    private final MemberService memberService;
    private final SpaceService spaceService;
    private final MemberSpaceService memberSpaceService;

    @GetMapping("/repositories/{keyword}")
    public ResponseEntity<?> getSearchedRepositoryList(@PathVariable String keyword){
        List<Space> spaceList = spaceService.search(keyword);

        List<SpaceDto> collect = spaceList.stream()
                .map(space -> {
                    Member member = memberService.findByUserId(space.getCreatedBy());
                    return new SpaceDto(space, member);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResult(collect));
    }

    @GetMapping("/members/{keyword}")
    public ResponseEntity<?> getSearchedMemberList(@PathVariable String keyword){
        List<Member> members = memberService.search(keyword);
        List<SearchMemberResponse> collect = members.stream()
                .map(member -> {
                    AtomicInteger likeCounts = new AtomicInteger();
                    AtomicInteger visitCounts = new AtomicInteger();

                    List<MemberSpace> spacesOfMember = memberSpaceService.findSpacesOfMember(member);
                    for (MemberSpace memberSpace: spacesOfMember) {
                        Space space = memberSpace.getSpace();
                        likeCounts.addAndGet(space.getLikeCount());
                        visitCounts.addAndGet(space.getVisitCount());
                    }

                    return new SearchMemberResponse(member.getId(),member.getUserId(), member.getName(),member.getEmail(), member.getDescription(), member.getProfile(), likeCounts.get(), visitCounts.get());
                }
                )
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResult(collect));
    }
}
