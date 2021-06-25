package com.mapgoblin.service;

import com.mapgoblin.api.dto.request.CompareDto;
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
import java.util.ArrayList;
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
    private final MemberRepository memberRepository;

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
                        LocalDateTime.parse(addedData.get("createdDate")),
                        addedData.get("geometry"),
                        RequestAction.INSERT);

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
                        LocalDateTime.parse(modifiedData.get("createdDate")),
                        modifiedData.get("geometry"),
                        RequestAction.UPDATE);

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
                        LocalDateTime.parse(deleteData.get("createdDate")),
                        deleteData.get("geometry"),
                        RequestAction.DELETE);

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
                        LocalDateTime.parse(layerData.get("createdDate")),
                        layerData.get("geometry"),
                        RequestAction.INSERT);

                request.addRequestData(requestData);

                requestDataRepository.save(requestData);
            }
        }

        return request.getId();
    }

    public Request findById(Long id){
        return requestRepository.findById(id).orElse(null);
    }

    /**
     * 요청사항 상세 조회
     *
     * @param requestId
     * @return
     */
    public HashMap<String, List<HashMap<String, String>>> findRequestInfoById(Long requestId) {
        HashMap<String, List<HashMap<String, String>>> result = new HashMap<>();

        Request request = requestRepository.findById(requestId).orElse(null);

        if(request != null) {

            // request information
            List<HashMap<String, String>> values = setRequestValues(request);

            result.put("values", values);

            // replies information
            List<HashMap<String, String>> replies = setReplyValues(request);

            if(replies != null && !replies.isEmpty()){
                result.put("replies", replies);
            }

            // request data information
            setRequestDataValues(result, request);

            return result;
        }else {
            System.out.println("request is null");
            return null;
        }

    }

    /**
     * 요청사항 정보 set
     *
     * @param request
     * @return
     */
    private List<HashMap<String, String>> setRequestValues(Request request) {
        List<HashMap<String, String>> values = new ArrayList<>();

        HashMap<String, String> value = new HashMap<>();

        value.put("title", request.getTitle());
        value.put("content", request.getContent());
        value.put("status", request.getStatus().toString());
        value.put("createdBy", request.getCreatedBy());

        values.add(value);

        return values;
    }

    /**
     * 요청사항 댓글 정보 set
     *
     * @param request
     * @return
     */
    private List<HashMap<String, String>> setReplyValues(Request request) {
        List<HashMap<String, String>> replies = new ArrayList<>();

        List<RequestReply> findReplies = requestReplyRepository.findByRequest(request).orElse(null);

        if(findReplies != null) {
            for (RequestReply findReply : findReplies) {
                HashMap<String, String> replyData = new HashMap<>();

                Member replyMember = memberRepository.findByUserId(findReply.getCreatedBy()).orElse(null);

                if(replyMember != null) {
                    replyData.put("author", findReply.getCreatedBy());
                    replyData.put("content", findReply.getContent());
                    replyData.put("name", replyMember.getName());
                    replyData.put("profile", replyMember.getProfile());
                    replyData.put("datetime", findReply.getCreatedDate().toString());

                    replies.add(replyData);
                }
            }

            return replies;
        }else{
            return null;
        }
    }

    /**
     * 요청사항 데이터 정보 set
     *
     * @param result
     * @param request
     */
    private void setRequestDataValues(HashMap<String, List<HashMap<String, String>>> result, Request request) {
        List<RequestData> requestDataList = request.getRequestDataList();

        if(requestDataList != null) {
            List<HashMap<String, String>> added = new ArrayList<>();
            List<HashMap<String, String>> modified = new ArrayList<>();
            List<HashMap<String, String>> delete = new ArrayList<>();
            List<HashMap<String, String>> layer = new ArrayList<>();

            for (RequestData requestData : requestDataList) {
                HashMap<String, String> data = new HashMap<>();

                if(requestData.getMapDataId() == null){
                    data.put("mapDataId", null);
                }else{
                    data.put("mapDataId", requestData.getMapDataId().toString());
                }

                data.put("layerId", requestData.getLayerId().toString());
                data.put("geometry", requestData.getGeometry());
                data.put("action", requestData.getAction().toString());
                data.put("name", requestData.getName());
                data.put("createdDate", requestData.getCreateDate().toString());

                if(requestData.getAction() == RequestAction.INSERT){
                    if(requestData.getMapDataId() == null){
                        //layer
                        layer.add(data);
                    }else{
                        //added
                        added.add(data);
                    }
                }else if(requestData.getAction() == RequestAction.UPDATE){
                    //modified
                    modified.add(data);
                }else if(requestData.getAction() == RequestAction.DELETE){
                    //delete
                    delete.add(data);
                }
            }

            if(!added.isEmpty()){
                result.put("added", added);
            }

            if(!modified.isEmpty()){
                result.put("modified", modified);
            }

            if(!delete.isEmpty()){
                result.put("delete", delete);
            }

            if(!layer.isEmpty()){
                result.put("layer", layer);
            }
        }
    }

    /**
     * 원본 지도, 클론 지도 데이터 비교
     *
     * @param hostId
     * @param clonedId
     * @return
     */
    public HashMap<String, List<CompareDto>> compareMapData(Long hostId, Long clonedId) {

        HashMap<String, List<CompareDto>> result = new HashMap<>();

        Space hostSpace = spaceRepository.findById(hostId).orElse(null);
        Space clonedSpace = spaceRepository.findById(clonedId).orElse(null);

        if(hostSpace != null && clonedSpace != null) {
            List<Layer> hostLayers = hostSpace.getMap().getLayers();
            List<Layer> clonedLayers = clonedSpace.getMap().getLayers();

            //새로 생성된 레이어
            List<CompareDto> createdLayer = detectNewLayer(clonedLayers);

            if(!createdLayer.isEmpty()){

                result.put("layer", createdLayer);
            }

            for (Layer hostLayer : hostLayers) {
                for (Layer clonedLayer : clonedLayers) {
                    if(clonedLayer.getHost() == hostLayer){
                        List<MapData> hostMapDataList = hostLayer.getMapDataList();
                        List<MapData> cloneMapDataList = clonedLayer.getMapDataList();

                        //지오메트리 hash
                        HashMap<String, MapData> hostGeom = new HashMap<>();
                        HashMap<String, MapData> cloneGeom = new HashMap<>();

                        for (MapData mapData : hostMapDataList) {
                            hostGeom.put(mapData.getGeometry(), mapData);
                        }

                        for (MapData mapData : cloneMapDataList) {
                            cloneGeom.put(mapData.getGeometry(), mapData);
                        }

                        List<String> geoms = new ArrayList<>();

                        // 삭제된 데이터
                        List<CompareDto> deleteList = detectData(hostGeom, cloneGeom, geoms, hostLayer.getId());

                        if(!deleteList.isEmpty()){
                            result.put("delete", deleteList);
                        }

                        //추가된 데이터
                        List<CompareDto> addedList = detectData(cloneGeom, hostGeom, geoms, hostLayer.getId());

                        if(!addedList.isEmpty()){
                            result.put("added", addedList);
                        }

                        //수정된 데이터
                        List<CompareDto> modifiedList = detectModifiedData(hostGeom, cloneGeom, hostLayer.getId());

                        if(!modifiedList.isEmpty()){
                            result.put("modified", modifiedList);
                        }

                        break;
                    }
                }
            }

        }

        return result;
    }


    /**
     * 새로 생긴 레이어 감지
     *
     * @param clonedLayers
     * @return
     */
    private List<CompareDto> detectNewLayer(List<Layer> clonedLayers) {
        List<CompareDto> result = new ArrayList<>();

        for (Layer clonedLayer : clonedLayers) {
            if(clonedLayer.getHost() == null){
                RequestData findRequestData = requestDataRepository.findByMapDataIdAndLayerIdAndStatus(null, clonedLayer.getId(), RequestStatus.WAITING).orElse(null);

                if(findRequestData == null){
                    CompareDto compareDto = new CompareDto();
                    compareDto.setId(clonedLayer.getId());
                    compareDto.setLayerId(clonedLayer.getId());
                    compareDto.setName(clonedLayer.getName());
                    compareDto.setCreatedDate(clonedLayer.getModifiedDate());
                    compareDto.setGeometry(null);

                    result.add(compareDto);
                }
            }
        }

        return result;
    }

    /**
     * 데이터 추가, 삭제 감지
     *
     * @param target
     * @param comparisonTarget
     * @param geoms
     * @param hostLayerId
     * @return
     */
    private List<CompareDto> detectData(HashMap<String, MapData> target, HashMap<String, MapData> comparisonTarget,
                                               List<String> geoms, Long hostLayerId) {
        List<CompareDto> result = new ArrayList<>();

        target.forEach((s, mapData) -> {
            if(!comparisonTarget.containsKey(s)){
                RequestData findRequestData = requestDataRepository.findByMapDataIdAndLayerIdAndStatus(mapData.getId(), hostLayerId, RequestStatus.WAITING).orElse(null);

                if(findRequestData == null){
                    CompareDto compareDto = new CompareDto();
                    compareDto.setId(mapData.getId());
                    compareDto.setLayerId(hostLayerId);
                    compareDto.setName(mapData.getName());
                    compareDto.setCreatedDate(mapData.getModifiedDate());
                    compareDto.setGeometry(s);

                    result.add(compareDto);

                }

                geoms.add(s);
            }
        });

        for (String geom : geoms) {
            target.remove(geom);
        }

        geoms.clear();

        return result;
    }

    /**
     * 수정된 데이터 감지
     *
     * @param hostGeom
     * @param cloneGeom
     * @param hostLayerId
     * @return
     */
    private List<CompareDto> detectModifiedData(HashMap<String, MapData> hostGeom, HashMap<String, MapData> cloneGeom,
                                                Long hostLayerId) {
        List<CompareDto> result = new ArrayList<>();

        cloneGeom.forEach((s, mapData) -> {
            MapData hostData = hostGeom.get(s);

            if(!mapData.equals(hostData)){
                RequestData findRequestData = requestDataRepository.findByMapDataIdAndLayerIdAndStatus(mapData.getId(), hostLayerId, RequestStatus.WAITING).orElse(null);

                if(findRequestData == null){
                    CompareDto compareDto = new CompareDto();
                    compareDto.setId(mapData.getId());
                    compareDto.setLayerId(hostLayerId);
                    compareDto.setName(mapData.getName());
                    compareDto.setCreatedDate(mapData.getModifiedDate());
                    compareDto.setGeometry(s);

                    result.add(compareDto);
                }
            }
        });

        return result;
    }

    /**
     * 원본 지도 변경 사항 감지
     *
     * @param hostId
     * @param clonedSpace
     * @return
     */
    public HashMap<String, List<CompareDto>> comparePullData(Long hostId, Space clonedSpace) {

        HashMap<String, List<CompareDto>> result = new HashMap<>();

        return result;
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
