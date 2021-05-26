package com.mapgoblin.api.controller;

import com.mapgoblin.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class IssueApi {

    private final IssueService issueService;


}
