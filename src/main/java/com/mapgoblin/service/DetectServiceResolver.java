package com.mapgoblin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.NotSupportedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DetectServiceResolver {

    private final List<DetectService> detectServices;

    public DetectService resolve(String type) throws NotSupportedException {
        return detectServices.stream()
                .filter(service -> service.support(type))
                .findFirst()
                .orElseThrow(NotSupportedException::new);
    }
}