package com.mapgoblin.service;

import com.mapgoblin.api.dto.request.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public Long save(Request request, RequestDataDto data){

        requestRepository.save(request);

        List<ChangeInfo> added = data.getAdded();

        if(added != null) {
            for (ChangeInfo addedData : added) {
                RequestData requestData = RequestData.create(addedData.getId(),
                addedData.getLayerId(),
                addedData.getName(),
                addedData.getCreateDate(),
                addedData.getGeometry(),
                RequestAction.INSERT);

                request.addRequestData(requestData);

                requestDataRepository.save(requestData);
            }
        }

        List<ChangeInfo> modified = data.getModified();

        if(modified != null) {
            for (ChangeInfo modifiedData : modified) {
                RequestData requestData = RequestData.create(modifiedData.getId(),
                        modifiedData.getLayerId(),
                        modifiedData.getName(),
                        modifiedData.getCreateDate(),
                        modifiedData.getGeometry(),
                        RequestAction.UPDATE);

                request.addRequestData(requestData);

                requestDataRepository.save(requestData);
            }
        }

        List<ChangeInfo> delete = data.getDelete();

        if(delete != null) {
            for (ChangeInfo deleteData : delete) {
                RequestData requestData = RequestData.create(deleteData.getId(),
                        deleteData.getLayerId(),
                        deleteData.getName(),
                        deleteData.getCreateDate(),
                        deleteData.getGeometry(),
                        RequestAction.DELETE);

                request.addRequestData(requestData);

                requestDataRepository.save(requestData);
            }
        }

        List<ChangeInfo> layer = data.getLayer();

        if(layer != null) {
            for (ChangeInfo layerData : layer) {
                RequestData requestData = RequestData.create(null,
                        layerData.getLayerId(),
                        layerData.getName(),
                        layerData.getCreateDate(),
                        layerData.getGeometry(),
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
    public RequestDataDto findRequestInfoById(Long requestId) {
        RequestDataDto result = new RequestDataDto();

        Request request = requestRepository.findById(requestId).orElse(null);

        if(request != null) {

            // request information
            List<ValueDto> values = setRequestValues(request);

            result.setValues(values);

            // replies information
            List<ReplyDto> replies = setReplyValues(request);

            if(replies != null && !replies.isEmpty()){
                result.setReplies(replies);
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
    private List<ValueDto> setRequestValues(Request request) {
        List<ValueDto> values = new ArrayList<>();

        ValueDto value = new ValueDto();

        value.setTitle(request.getTitle());
        value.setContent(request.getContent());
        value.setStatus(request.getStatus());
        value.setCreatedBy(request.getCreatedBy());

        values.add(value);

        return values;
    }

    /**
     * 요청사항 댓글 정보 set
     *
     * @param request
     * @return
     */
    private List<ReplyDto> setReplyValues(Request request) {
        List<ReplyDto> replies = new ArrayList<>();

        List<RequestReply> findReplies = requestReplyRepository.findByRequest(request).orElse(null);

        if(findReplies != null) {
            for (RequestReply findReply : findReplies) {
                ReplyDto replyData = new ReplyDto();

                Member replyMember = memberRepository.findByUserId(findReply.getCreatedBy()).orElse(null);

                if(replyMember != null) {
                    replyData.setAuthor(findReply.getCreatedBy());
                    replyData.setContent(findReply.getContent());
                    replyData.setName(replyMember.getName());
                    replyData.setProfile(replyMember.getProfile());
                    replyData.setDatetime(findReply.getCreatedDate());

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
    private void setRequestDataValues(RequestDataDto result, Request request) {
        List<RequestData> requestDataList = request.getRequestDataList();

        if(requestDataList != null) {
            for (RequestData requestData : requestDataList) {
                ChangeInfo data = ChangeInfo.byRequestData(requestData);

                if(requestData.getAction() == RequestAction.INSERT){
                    if(requestData.getMapDataId() == null){
                        //layer
                        result.getLayer().add(data);
                    }else{
                        //added
                        result.getAdded().add(data);
                    }
                }else if(requestData.getAction() == RequestAction.UPDATE){
                    //modified
                    result.getModified().add(data);
                }else if(requestData.getAction() == RequestAction.DELETE){
                    //delete
                    result.getDelete().add(data);
                }
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
                        //지오메트리 hash
                        HashMap<String, MapData> hostGeom = getGeometryHashMap(hostLayer);
                        HashMap<String, MapData> cloneGeom = getGeometryHashMap(clonedLayer);

                        // 삭제된 데이터
                        List<CompareDto> deleteList = detectData(hostGeom, cloneGeom, hostLayer.getId());

                        if(!deleteList.isEmpty()){
                            result.put("delete", deleteList);
                        }

                        //추가된 데이터
                        List<CompareDto> addedList = detectData(cloneGeom, hostGeom, hostLayer.getId());

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

    private HashMap<String, MapData> getGeometryHashMap(Layer layer) {
        List<MapData> mapDataList = layer.getMapDataList();

        HashMap<String, MapData> result = new HashMap<>();

        for (MapData mapData : mapDataList) {
            result.put(mapData.getGeometry(), mapData);
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
                CompareDto compareDto = getCompareDto(clonedLayer);

                if(compareDto != null) {
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
     * @param hostLayerId
     * @return
     */
    private List<CompareDto> detectData(HashMap<String, MapData> target, HashMap<String, MapData> comparisonTarget,
                                               Long hostLayerId) {
        List<CompareDto> result = new ArrayList<>();
        List<String> detectGeoms = new ArrayList<>();

        target.forEach((geom, mapData) -> {
            if(!comparisonTarget.containsKey(geom)){
                CompareDto compareDto = getCompareDto(geom, mapData, hostLayerId);

                if(compareDto != null) {
                    result.add(compareDto);
                }

                detectGeoms.add(geom);
            }
        });

        for (String geom : detectGeoms) {
            target.remove(geom);
        }

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

        cloneGeom.forEach((geom, mapData) -> {
            MapData hostData = hostGeom.get(geom);

            if(!mapData.equals(hostData)){
                CompareDto compareDto = getCompareDto(geom, mapData, hostLayerId);

                if(compareDto != null) {
                    result.add(compareDto);
                }
            }
        });

        return result;
    }

    private CompareDto getCompareDto(String geom, MapData mapData, Long hostLayerId) {
        RequestData findRequestData = requestDataRepository.findByMapDataIdAndLayerIdAndStatus(mapData.getId(), hostLayerId, RequestStatus.WAITING).orElse(null);

        if(findRequestData == null){
            CompareDto compareDto = new CompareDto();
            compareDto.setId(mapData.getId());
            compareDto.setLayerId(hostLayerId);
            compareDto.setName(mapData.getName());
            compareDto.setCreatedDate(mapData.getModifiedDate());
            compareDto.setGeometry(geom);

            return compareDto;
        }

        return null;
    }

    private CompareDto getCompareDto(Layer layer) {
        RequestData findRequestData = requestDataRepository.findByMapDataIdAndLayerIdAndStatus(null, layer.getId(), RequestStatus.WAITING).orElse(null);

        if(findRequestData == null){
            CompareDto compareDto = new CompareDto();
            compareDto.setId(layer.getId());
            compareDto.setLayerId(layer.getId());
            compareDto.setName(layer.getName());
            compareDto.setCreatedDate(layer.getModifiedDate());
            compareDto.setGeometry(null);

            return compareDto;
        }

        return null;
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

        Space hostSpace = spaceRepository.findById(hostId).orElse(null);

        if(hostSpace != null && clonedSpace != null) {
            List<Layer> hostLayers = hostSpace.getMap().getLayers();
            List<Layer> clonedLayers = clonedSpace.getMap().getLayers();

            //새로 생성된 레이어
            List<CompareDto> createdLayer = detectOriginCreateLayer(hostLayers, clonedLayers);

            if(!createdLayer.isEmpty()){

                result.put("layer", createdLayer);
            }

            for (Layer clonedLayer : clonedLayers) {
                for (Layer hostLayer : hostLayers) {
                    if (clonedLayer.getHost().getId().equals(hostLayer.getId())) {
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
                        List<CompareDto> deleteList = detectOriginAddedAndDeletedData(cloneGeom, hostGeom, geoms, clonedLayer.getId());

                        for (String geom : geoms) {
                            hostGeom.remove(geom);
                        }

                        geoms.clear();

                        if(!deleteList.isEmpty()){
                            result.put("delete", deleteList);
                        }

                        //추가된 데이터
                        List<CompareDto> addedList = detectOriginAddedAndDeletedData(hostGeom, cloneGeom, geoms, clonedLayer.getId());

                        for (String geom : geoms) {
                            hostGeom.remove(geom);
                        }

                        geoms.clear();

                        if(!addedList.isEmpty()){
                            result.put("added", addedList);
                        }

                        //수정된 데이터
                        List<CompareDto> modifiedList = detectOriginModifiedData(hostGeom, cloneGeom, clonedLayer.getId());

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
     * 원본 지도에 추가된 레이어 감지
     *
     * @param hostLayers
     * @param clonedLayers
     * @return
     */
    private List<CompareDto> detectOriginCreateLayer(List<Layer> hostLayers, List<Layer> clonedLayers) {
        //새로 생성된 레이어
        List<CompareDto> result = new ArrayList<>();

        for (Layer hostLayer : hostLayers) {
            boolean layerCheck = true;

            for (Layer clonedLayer : clonedLayers) {
                if(clonedLayer.getHost().getId().equals(hostLayer.getId())){
                    layerCheck = false;
                    break;
                }
            }

            if(layerCheck){
                CompareDto compareDto = new CompareDto();
                compareDto.setId(hostLayer.getId());
                compareDto.setLayerId(hostLayer.getId());
                compareDto.setName(hostLayer.getName());
                compareDto.setCreatedDate(hostLayer.getModifiedDate());
                compareDto.setGeometry(null);

                result.add(compareDto);
            }
        }

        return result;
    }

    /**
     * 원본 지도 추가, 삭제 데이터 감지
     *
     * @param target
     * @param comparisonTarget
     * @param geoms
     * @param clonedLayerId
     * @return
     */
    private List<CompareDto> detectOriginAddedAndDeletedData(HashMap<String, MapData> target, HashMap<String, MapData> comparisonTarget,
                                                             List<String> geoms, Long clonedLayerId) {
        List<CompareDto> result = new ArrayList<>();

        target.forEach((s, mapData) -> {
            if(!comparisonTarget.containsKey(s)){
                CompareDto compareDto = new CompareDto();
                compareDto.setId(mapData.getId());
                compareDto.setLayerId(clonedLayerId);
                compareDto.setName(mapData.getName());
                compareDto.setCreatedDate(mapData.getModifiedDate());
                compareDto.setGeometry(s);

                result.add(compareDto);

                geoms.add(s);
            }
        });

        return result;
    }

    /**
     * 원본지도 데이터 수정 사항 감지
     *
     * @param hostGeom
     * @param cloneGeom
     * @param clonedLayerId
     * @return
     */
    private List<CompareDto> detectOriginModifiedData(HashMap<String, MapData> hostGeom, HashMap<String, MapData> cloneGeom,
                                                      Long clonedLayerId) {
        //수정된 데이터
        List<CompareDto> result = new ArrayList<>();

        hostGeom.forEach((s, mapData) -> {
            MapData hostData = cloneGeom.get(s);

            if(!mapData.equals(hostData)){
                CompareDto compareDto = new CompareDto();
                compareDto.setId(mapData.getId());
                compareDto.setLayerId(clonedLayerId);
                compareDto.setName(mapData.getName());
                compareDto.setCreatedDate(mapData.getModifiedDate());
                compareDto.setGeometry(s);

                result.add(compareDto);
            }
        });

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
    public void merge(Long spaceId, Long requestId) throws CloneNotSupportedException {
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
