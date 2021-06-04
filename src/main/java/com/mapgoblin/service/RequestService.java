package com.mapgoblin.service;

import com.mapgoblin.api.dto.request.RequestDto;
import com.mapgoblin.domain.Request;
import com.mapgoblin.domain.RequestData;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.RequestAction;
import com.mapgoblin.repository.RequestDataRepository;
import com.mapgoblin.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestDataRepository requestDataRepository;

    public Page<RequestDto> findRequestsOfSpace(Space space, Pageable pageable) {

        Page<Request> requests = requestRepository.findBySpace(space, pageable);

        return requests.map(RequestDto::new);
    }

    @Transactional
    public Long save(Request request, HashMap<String, List<HashMap<String, String>>> data){

        requestRepository.save(request);

        if(data.containsKey("added")){
            List<HashMap<String, String>> added = data.get("added");

            for (HashMap<String, String> addedData : added) {
                RequestData requestData = RequestData.create(Long.parseLong(addedData.get("id")),
                        Long.parseLong(addedData.get("layerId")),
                        addedData.get("name"),
                        LocalDateTime.parse(addedData.get("createdDate")), RequestAction.INSERT);

                request.addRequestData(requestData);

                requestDataRepository.save(requestData);
            }
        }

        if(data.containsKey("modified")){
            List<HashMap<String, String>> modified = data.get("modified");

            for (HashMap<String, String> modifiedData : modified) {
                RequestData requestData = RequestData.create(Long.parseLong(modifiedData.get("id")),
                        Long.parseLong(modifiedData.get("layerId")),
                        modifiedData.get("name"),
                        LocalDateTime.parse(modifiedData.get("createdDate")), RequestAction.UPDATE);

                request.addRequestData(requestData);

                requestDataRepository.save(requestData);
            }
        }

        if(data.containsKey("delete")){
            List<HashMap<String, String>> delete = data.get("delete");

            for (HashMap<String, String> deleteData : delete) {
                RequestData requestData = RequestData.create(Long.parseLong(deleteData.get("id")),
                        Long.parseLong(deleteData.get("layerId")),
                        deleteData.get("name"),
                        LocalDateTime.parse(deleteData.get("createdDate")), RequestAction.DELETE);

                request.addRequestData(requestData);

                requestDataRepository.save(requestData);
            }
        }

        if(data.containsKey("layer")){
            List<HashMap<String, String>> layer = data.get("layer");

            for (HashMap<String, String> layerData : layer) {
                RequestData requestData = RequestData.create(null,
                        Long.parseLong(layerData.get("layerId")),
                        layerData.get("name"),
                        LocalDateTime.parse(layerData.get("createdDate")), RequestAction.INSERT);

                request.addRequestData(requestData);

                requestDataRepository.save(requestData);
            }
        }

        return request.getId();
    }

    public Request findById(Long id){
        return requestRepository.findById(id).orElse(null);
    }
}
