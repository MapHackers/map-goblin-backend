package com.mapgoblin.service;

import com.mapgoblin.domain.Request;
import com.mapgoblin.domain.RequestData;
import com.mapgoblin.repository.RequestDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestDataService {
    private final RequestDataRepository requestDataRepository;

    public RequestData findByMapDataIdAndLayerId(Long mapDataId, Long layerId){
        return requestDataRepository.findByMapDataIdAndLayerId(mapDataId, layerId).orElse(null);
    }

    public List<RequestData> findDataByRequest(Request request){
        return requestDataRepository.findByRequest(request).orElse(null);
    }
}
