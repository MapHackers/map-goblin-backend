package com.mapgoblin.api.controller;

import com.mapgoblin.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RequestApi {

    private final RequestService requestService;

}
