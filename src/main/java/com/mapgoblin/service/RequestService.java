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
                        addedData.getCreatedDate(),
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
                        modifiedData.getCreatedDate(),
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
                        deleteData.getCreatedDate(),
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
                        layerData.getCreatedDate(),
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
                ChangeInfo data = ChangeInfo.createdByRequestData(requestData);

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

    @Transactional
    public RequestReply replySave(RequestReply reply){
        return requestReplyRepository.save(reply);
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

                            mapData.getReviews().forEach(reviewRepository::save);
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

                    mapData.getReviews().forEach(reviewRepository::save);
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
