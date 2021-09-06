package com.mapgoblin.service;

import com.mapgoblin.api.dto.request.RequestDataDto;
import com.mapgoblin.domain.Space;

public interface DetectService {

    boolean support(String type);
    RequestDataDto compareMapData(Long hostId, Space clonedSpace);
}