package com.mapgoblin.api.controller;

import com.mapgoblin.domain.Member;
import com.mapgoblin.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class IssueApi {

    private final IssueService issueService;

    @PostMapping("/issues")
    public ResponseEntity<?> create(@RequestBody HashMap<String, String> request){
        

        return ResponseEntity.ok("");
    }


}
