package com.mapgoblin.service;

import com.mapgoblin.api.dto.request.RequestDto;
import com.mapgoblin.api.dto.space.SpaceResponse;
import com.mapgoblin.domain.*;
import com.mapgoblin.domain.base.RequestAction;
import com.mapgoblin.domain.base.RequestStatus;
import com.mapgoblin.domain.base.RequestTag;
import com.mapgoblin.domain.mapdata.MapData;
import com.mapgoblin.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestReplyRepository requestReplyRepository;
    private final RequestDataRepository requestDataRepository;
    private final SpaceRepository spaceRepository;
    private final LayerRepository layerRepository;
    private final MapDataRepository mapDataRepository;
    private final ReviewRepository reviewRepository;

    public Page<RequestDto> findRequestsOfSpace(Space space, RequestStatus status, Pageable pageable) {

        Page<Request> requests = requestRepository.findBySpaceAndStatus(space, status, pageable);

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

    @Transactional
    public RequestReply replySave(RequestReply reply){
        return requestReplyRepository.save(reply);
    }

    public List<RequestReply> findRepliesByRequest(Request request) {
        return requestReplyRepository.findByRequest(request).orElse(null);
    }

    @Transactional
    public void merger(Long spaceId, Long requestId) throws CloneNotSupportedException {
        Request findRequest = requestRepository.findById(requestId).orElse(null);

        List<RequestData> requestDataList = requestDataRepository.findByRequest(findRequest).orElse(null);

        Space space = spaceRepository.findById(spaceId).orElse(null);

        for (RequestData requestData : requestDataList) {

            if(requestData.getAction() == RequestAction.INSERT){
                if(requestData.getMapDataId() == null){
                    //layer
                    Layer layer = layerRepository.findById(requestData.getLayerId()).orElse(null);
                    if(layer != null){
                        Layer clone = (Layer) layer.clone();
                        space.getMap().addLayer(clone);

                        layerRepository.save(clone);

                        clone.getMapDataList().forEach(mapData -> {
                            mapDataRepository.save(mapData);

                            mapData.getReviews().forEach(review -> {
                                reviewRepository.save(review);
                            });
                        });

                        layer.setHost(clone);
                    }
                }else{
                    //added
                    Layer targetLayer = null;
                    List<Layer> layers = space.getMap().getLayers();

                    for (Layer layer : layers) {
                        if(layer.getId().equals(requestData.getLayerId())){
                            targetLayer = layer;
                            break;
                        }
                    }

                    MapData mapData = mapDataRepository.findById(requestData.getMapDataId()).orElse(null);

                    MapData clone = (MapData) mapData.clone();
                    targetLayer.addMapData(clone);

                    mapDataRepository.save(clone);

                    mapData.getReviews().forEach(review -> {
                        reviewRepository.save(review);
                    });
                }
            }else if(requestData.getAction() == RequestAction.UPDATE){
                //modified
                MapData modifiedMapData = mapDataRepository.findById(requestData.getMapDataId()).orElse(null);

                MapData originData = mapDataRepository.findByGeometryAndLayer(modifiedMapData.getGeometry(), modifiedMapData.getLayer().getHost()).orElse(null);

                originData.setName(modifiedMapData.getName());
                originData.setDescription(modifiedMapData.getDescription());
                originData.setThumbnail(modifiedMapData.getThumbnail());

            }else if(requestData.getAction() == RequestAction.DELETE){
                //delete

                MapData mapData = mapDataRepository.findById(requestData.getMapDataId()).orElse(null);

                mapDataRepository.delete(mapData);

            }

            requestData.setStatus(RequestStatus.ACCEPTED);

        }

        findRequest.setStatus(RequestStatus.ACCEPTED);
        findRequest.setTag(RequestTag.MERGE);
    }

    @Transactional
    public void denied(Long requestId){

        Request request = requestRepository.findById(requestId).orElse(null);

        if(request != null){
            List<RequestData> requestDataList = requestDataRepository.findByRequest(request).orElse(null);

            if(requestDataList != null && requestDataList.size() > 0){
                for (RequestData requestData : requestDataList) {
                    requestData.setStatus(RequestStatus.DENIED);
                }
            }

            request.setStatus(RequestStatus.DENIED);
            request.setTag(RequestTag.DENIED);
        }
    }
}
