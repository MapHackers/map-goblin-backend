package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.space.CreateSpaceRequest;
import com.mapgoblin.api.dto.space.CreateSpaceResponse;
import com.mapgoblin.api.dto.space.SpaceDto;
import com.mapgoblin.domain.Map;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.MemberSpace;
import com.mapgoblin.domain.Space;
import com.mapgoblin.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/repositories")
@RequiredArgsConstructor
public class SpaceApi {

    private final SpaceService spaceService;

    /**
     * Get all repositories
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<?> get() {

        List<Space> spaceList = spaceService.findAll();

        List<SpaceDto> collect = spaceList.stream()
                .map(space -> new SpaceDto(space))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResult(collect));
    }

    /**
     * Create repository
     *
     * @param request
     * @param member
     * @return
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateSpaceRequest request, @AuthenticationPrincipal Member member) {

        CreateSpaceResponse response = spaceService.create(member.getId(), request);

        return ResponseEntity.ok(response);
    }
}
