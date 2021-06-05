package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.request.CompareDto;
import com.mapgoblin.api.dto.request.RequestDto;
import com.mapgoblin.api.dto.space.SpaceResponse;
import com.mapgoblin.domain.*;
import com.mapgoblin.domain.base.RequestAction;
import com.mapgoblin.domain.base.RequestStatus;
import com.mapgoblin.domain.mapdata.MapData;
import com.mapgoblin.service.MemberService;
import com.mapgoblin.service.RequestDataService;
import com.mapgoblin.service.RequestService;
import com.mapgoblin.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RequestApi {

    private final RequestService requestService;
    private final SpaceService spaceService;
    private final MemberService memberService;
    private final RequestDataService requestDataService;

    @GetMapping("/{userId}/repositories/{repositoryName}/requests")
    public ResponseEntity<?> getRequestList(@PathVariable String userId, @PathVariable String repositoryName, @RequestParam String status,
                                            @PageableDefault(size = 8, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {


        Member findMember = memberService.findByUserId(userId);

        List<SpaceResponse> target = spaceService.findOne(findMember.getId(), repositoryName);

        if (target.size() == 1 && target.get(0) != null){
            Space space = spaceService.findById(target.get(0).getId());

            Page<RequestDto> result = requestService.findRequestsOfSpace(space, RequestStatus.valueOf(status), pageable);

            return ResponseEntity.ok(result);

        }else{
            return ApiResult.errorMessage("존재하지 않는 지도입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}/repositories/{repositoryName}/requests/{requestId}")
    public ResponseEntity<?> getRequestInfo(@PathVariable String userId, @PathVariable String repositoryName, @PathVariable Long requestId){

        HashMap<String, List<HashMap<String, String>>> result = new HashMap<>();

        Request request = requestService.findById(requestId);

        // request title, content
        List<HashMap<String, String>> values = new ArrayList<>();

        HashMap<String, String> value = new HashMap<>();

        value.put("title", request.getTitle());
        value.put("content", request.getContent());
        value.put("status", request.getStatus().toString());
        value.put("createdBy", request.getCreatedBy());

        values.add(value);

        result.put("values", values);

        // 댓글
        List<HashMap<String, String>> replies = new ArrayList<>();

        List<RequestReply> findReplies = requestService.findRepliesByRequest(request);

        for (RequestReply findReply : findReplies) {
            HashMap<String, String> replyData = new HashMap<>();

            replyData.put("author", findReply.getCreatedBy());
            replyData.put("content", findReply.getContent());
            replyData.put("datetime", findReply.getCreatedDate().toString());

            replies.add(replyData);
        }

        if(!replies.isEmpty()){
            result.put("replies", replies);
        }

        // request data
        List<RequestData> requestDataList = request.getRequestDataList();
        List<HashMap<String, String>> added = new ArrayList<>();
        List<HashMap<String, String>> modified = new ArrayList<>();
        List<HashMap<String, String>> delete = new ArrayList<>();
        List<HashMap<String, String>> layer = new ArrayList<>();

        for (RequestData requestData : requestDataList) {

            HashMap<String, String> data = new HashMap<>();

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

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{userId}/repositories/{repositoryName}/requests")
    public ResponseEntity<?> create(@RequestBody HashMap<String, List<HashMap<String, String>>> request,
                                    @PathVariable String userId, @PathVariable String repositoryName) {

        Member findMember = memberService.findByUserId(userId);

        List<SpaceResponse> target = spaceService.findOne(findMember.getId(), repositoryName);

        if (target.size() == 1 && target.get(0) != null){
            List<HashMap<String, String>> values = request.get("values");
            Space findSpace = spaceService.findById(target.get(0).getId());

            Request request1 = Request.create(values.get(0).get("title"), values.get(1).get("content"), findSpace);

            HashMap<String, Long> result = new HashMap<>();

            result.put("requestId", requestService.save(request1, request));

            return ResponseEntity.ok(result);

        }else{
            return ApiResult.errorMessage("해당 지도가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{userId}/repositories/{repositoryName}/compare")
    public ResponseEntity<?> compareOriginClone(@PathVariable String userId, @PathVariable String repositoryName,
                                                @AuthenticationPrincipal Member member) {

        HashMap<String, List<CompareDto>> result = new HashMap<>();

        Member findMember = memberService.findByUserId(userId);

        List<SpaceResponse> target = spaceService.findOne(findMember.getId(), repositoryName);

        if (target.size() == 1 && target.get(0) != null){

            List<SpaceResponse> byMemberIdAndHostId = spaceService.findByMemberIdAndHostId(member.getId(), target.get(0).getId());

            if(byMemberIdAndHostId.size() == 1 && byMemberIdAndHostId.get(0) != null){
                Space hostSpace = spaceService.findById(target.get(0).getId());
                Space clonedSpace = spaceService.findById(byMemberIdAndHostId.get(0).getId());

                List<Layer> hostLayers = hostSpace.getMap().getLayers();
                List<Layer> clonedLayers = clonedSpace.getMap().getLayers();

                //새로 생성된 레이어
                List<CompareDto> createdLayer = new ArrayList<>();

                for (Layer clonedLayer : clonedLayers) {
                    if(clonedLayer.getHost() == null){
                        RequestData findRequestData = requestDataService.findByMapDataIdAndLayerId(null, clonedLayer.getId());
                        if(findRequestData == null || findRequestData.getStatus() == RequestStatus.DENIED){
                            CompareDto compareDto = new CompareDto();
                            compareDto.setId(clonedLayer.getId());
                            compareDto.setLayerId(clonedLayer.getId());
                            compareDto.setName(clonedLayer.getName());
                            compareDto.setCreatedDate(clonedLayer.getModifiedDate());

                            createdLayer.add(compareDto);
                        }
                    }
                }

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
                            List<CompareDto> deleteList = new ArrayList<>();

                            hostGeom.forEach((s, mapData) -> {
                                if(!cloneGeom.containsKey(s)){
                                    RequestData findRequestData = requestDataService.findByMapDataIdAndLayerId(mapData.getId(), hostLayer.getId());
                                    if(findRequestData == null || findRequestData.getStatus() == RequestStatus.DENIED){
                                        CompareDto compareDto = new CompareDto();
                                        compareDto.setId(mapData.getId());
                                        compareDto.setLayerId(hostLayer.getId());
                                        compareDto.setName(mapData.getName());
                                        compareDto.setCreatedDate(mapData.getModifiedDate());

                                        deleteList.add(compareDto);

                                        geoms.add(s);
                                    }
                                }
                            });

                            for (String geom : geoms) {
                                hostGeom.remove(geom);
                            }

                            geoms.clear();

                            if(!deleteList.isEmpty()){
                                result.put("delete", deleteList);
                            }

                            //추가된 데이터
                            List<CompareDto> addedList = new ArrayList<>();

                            cloneGeom.forEach((s, mapData) -> {
                                if(!hostGeom.containsKey(s)){
                                    RequestData findRequestData = requestDataService.findByMapDataIdAndLayerId(mapData.getId(), hostLayer.getId());
                                    if(findRequestData == null || findRequestData.getStatus() == RequestStatus.DENIED){
                                        CompareDto compareDto = new CompareDto();
                                        compareDto.setId(mapData.getId());
                                        compareDto.setLayerId(hostLayer.getId());
                                        compareDto.setName(mapData.getName());
                                        compareDto.setCreatedDate(mapData.getModifiedDate());

                                        addedList.add(compareDto);

                                        geoms.add(s);
                                    }
                                }
                            });

                            for (String geom : geoms) {
                                cloneGeom.remove(geom);
                            }

                            if(!addedList.isEmpty()){
                                result.put("added", addedList);
                            }

                            //수정된 데이터
                            List<CompareDto> modifiedList = new ArrayList<>();

                            cloneGeom.forEach((s, mapData) -> {
                                MapData hostData = hostGeom.get(s);

                                if(!mapData.equals(hostData)){
                                    RequestData findRequestData = requestDataService.findByMapDataIdAndLayerId(mapData.getId(), hostLayer.getId());
                                    if(findRequestData == null || findRequestData.getStatus() == RequestStatus.DENIED){
                                        CompareDto compareDto = new CompareDto();
                                        compareDto.setId(mapData.getId());
                                        compareDto.setLayerId(hostLayer.getId());
                                        compareDto.setName(mapData.getName());
                                        compareDto.setCreatedDate(mapData.getModifiedDate());

                                        modifiedList.add(compareDto);
                                    }
                                }
                            });

                            if(!modifiedList.isEmpty()){
                                result.put("modified", modifiedList);
                            }

                            break;
                        }
                    }
                }

                if(result.isEmpty()){
                    return ApiResult.errorMessage("변경된 데이터가 없습니다.", HttpStatus.OK);
                }

                return ResponseEntity.ok(result);

            }else{
                return ApiResult.errorMessage("클론한 지도가 없습니다.", HttpStatus.OK);
            }

        }else{
            return ApiResult.errorMessage("존재하지 않는 지도입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{userId}/repositories/{repositoryName}/requests/{requestId}/reply")
    public ResponseEntity<?> saveReply(@RequestBody HashMap<String, String> request,
                                         @PathVariable String userId, @PathVariable String repositoryName, @PathVariable Long requestId){

        HashMap<String, String> result = new HashMap<>();

        Request findRequest = requestService.findById(requestId);

        RequestReply reply = RequestReply.create(request.get("content"));

        findRequest.addReply(reply);

        RequestReply saved = requestService.replySave(reply);

        result.put("author", saved.getCreatedBy());
        result.put("content", saved.getContent());
        result.put("datetime", saved.getCreatedDate().toString());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{userId}/repositories/{repositoryName}/requests/{requestId}/merge")
    public ResponseEntity<?> mergeData(@PathVariable String userId, @PathVariable String repositoryName, @PathVariable Long requestId) throws CloneNotSupportedException {

        Member findMember = memberService.findByUserId(userId);

        List<SpaceResponse> target = spaceService.findOne(findMember.getId(), repositoryName);

        if (target.size() == 1 && target.get(0) != null){

            requestService.merger(target.get(0).getId(), requestId);

            return ResponseEntity.ok("merge");
        }else{
            return ApiResult.errorMessage("존재하지 않는 지도입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{userId}/repositories/{repositoryName}/requests/{requestId}/denied")
    public ResponseEntity<?> deniedData(@PathVariable String userId, @PathVariable String repositoryName, @PathVariable Long requestId){

        requestService.denied(requestId);

        return ResponseEntity.ok("denied");
    }
}
