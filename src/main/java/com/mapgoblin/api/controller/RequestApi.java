package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.request.CompareDto;
import com.mapgoblin.api.dto.request.RequestDto;
import com.mapgoblin.api.dto.space.SpaceResponse;
import com.mapgoblin.domain.*;
import com.mapgoblin.domain.base.AlarmType;
import com.mapgoblin.domain.base.RequestAction;
import com.mapgoblin.domain.base.RequestStatus;
import com.mapgoblin.domain.base.SourceType;
import com.mapgoblin.domain.mapdata.MapData;
import com.mapgoblin.service.*;
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
    private final AlarmService alarmService;
    private final MemberSpaceService memberSpaceService;

    /**
     * 요청사항 리스트 조회
     *
     * @param userId
     * @param repositoryName
     * @param status
     * @param pageable
     * @return
     */
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

    /**
     * 요청사항 상세 조회
     *
     * @param userId
     * @param repositoryName
     * @param requestId
     * @return
     */
    @GetMapping("/{userId}/repositories/{repositoryName}/requests/{requestId}")
    public ResponseEntity<?> getRequestInfo(@PathVariable String userId, @PathVariable String repositoryName, @PathVariable Long requestId){

        HashMap<String, List<HashMap<String, String>>> result = requestService.findRequestInfoById(requestId);

        if(result != null){
            return ResponseEntity.ok(result);
        }else{
            return ApiResult.errorMessage("해당 요청이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 요청사항 생성
     *
     * @param request
     * @param userId
     * @param repositoryName
     * @return
     */
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

            alarmService.save(findSpace.getId(), AlarmType.REQUEST);

            return ResponseEntity.ok(result);

        }else{
            return ApiResult.errorMessage("해당 지도가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 원본지도와 복제지도 변경사항 감지
     *
     * @param userId
     * @param repositoryName
     * @param member
     * @return
     */
    @GetMapping("/{userId}/repositories/{repositoryName}/compare")
    public ResponseEntity<?> compareOriginClone(@PathVariable String userId, @PathVariable String repositoryName,
                                                @AuthenticationPrincipal Member member) {

        HashMap<String, List<CompareDto>> result;

        Member findMember = memberService.findByUserId(userId);

        List<SpaceResponse> target = spaceService.findOne(findMember.getId(), repositoryName);

        if (target.size() == 1 && target.get(0) != null){

            List<SpaceResponse> byMemberIdAndHostId = spaceService.findByMemberIdAndHostId(member.getId(), target.get(0).getId());

            if(byMemberIdAndHostId.size() == 1 && byMemberIdAndHostId.get(0) != null){

                result = requestService.compareMapData(target.get(0).getId(), byMemberIdAndHostId.get(0).getId());

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

    /**
     * 원본지도 변경 데이터 받아오기
     *
     * @param userId
     * @param repositoryName
     * @return
     * @throws CloneNotSupportedException
     */
    @PostMapping("/{userId}/repositories/{repositoryName}/pull")
    public ResponseEntity<?> pullData(@PathVariable String userId, @PathVariable String repositoryName) throws CloneNotSupportedException {

        return ResponseEntity.ok("");
    }

    /**
     *
     *
     * @param userId
     * @param repositoryName
     * @param member
     * @return
     */
    @GetMapping("/{userId}/repositories/{repositoryName}/pull/compare")
    public ResponseEntity<?> comparePullData(@PathVariable String userId, @PathVariable String repositoryName, @AuthenticationPrincipal Member member){

        HashMap<String, List<CompareDto>> result = new HashMap<>();

        Member findMember = memberService.findByUserId(userId);

        List<SpaceResponse> target = spaceService.findOne(findMember.getId(), repositoryName);

        if (target.size() == 1 && target.get(0) != null && target.get(0).getSource() == SourceType.CLONE){

            List<MemberSpace> spacesOfMember = memberSpaceService.findSpacesOfMember(member);
            Space clonedSpace = spaceService.findById(target.get(0).getId());
            boolean ownerCheck = true;

            for (MemberSpace memberSpace : spacesOfMember) {
                if(memberSpace.getSpace() == clonedSpace){
                    ownerCheck = false;
                    break;
                }
            }

            if(ownerCheck){
                return ApiResult.errorMessage("주인이 아닙니다.", HttpStatus.OK);
            }

            Space hostSpace = spaceService.findById(target.get(0).getHostId());


            List<Layer> hostLayers = hostSpace.getMap().getLayers();
            List<Layer> clonedLayers = clonedSpace.getMap().getLayers();

            //새로 생성된 레이어
            List<CompareDto> createdLayer = new ArrayList<>();

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

                    createdLayer.add(compareDto);
                }
            }

            if(!createdLayer.isEmpty()){

                result.put("layer", createdLayer);
            }

            for (Layer clonedLayer : clonedLayers) {
                for (Layer hostLayer : hostLayers) {
                    if(clonedLayer.getHost().getId().equals(hostLayer.getId())){
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

                        cloneGeom.forEach((s, mapData) -> {
                            if(!hostGeom.containsKey(s)){
                                CompareDto compareDto = new CompareDto();
                                compareDto.setId(mapData.getId());
                                compareDto.setLayerId(clonedLayer.getId());
                                compareDto.setName(mapData.getName());
                                compareDto.setCreatedDate(mapData.getModifiedDate());
                                compareDto.setGeometry(s);

                                deleteList.add(compareDto);

                                geoms.add(s);
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

                        hostGeom.forEach((s, mapData) -> {
                            if(!cloneGeom.containsKey(s)){
                                CompareDto compareDto = new CompareDto();
                                compareDto.setId(mapData.getId());
                                compareDto.setLayerId(clonedLayer.getId());
                                compareDto.setName(mapData.getName());
                                compareDto.setCreatedDate(mapData.getModifiedDate());
                                compareDto.setGeometry(s);

                                addedList.add(compareDto);

                                geoms.add(s);
                            }
                        });

                        for (String geom : geoms) {
                            hostGeom.remove(geom);
                        }

                        if(!addedList.isEmpty()){
                            result.put("added", addedList);
                        }

                        //수정된 데이터
                        List<CompareDto> modifiedList = new ArrayList<>();

                        hostGeom.forEach((s, mapData) -> {
                            MapData hostData = cloneGeom.get(s);

                            if(!mapData.equals(hostData)){
                                CompareDto compareDto = new CompareDto();
                                compareDto.setId(mapData.getId());
                                compareDto.setLayerId(clonedLayer.getId());
                                compareDto.setName(mapData.getName());
                                compareDto.setCreatedDate(mapData.getModifiedDate());
                                compareDto.setGeometry(s);

                                modifiedList.add(compareDto);
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
    }

    /**
     * 요청사항 댓글 조회
     *
     * @param request
     * @param userId
     * @param repositoryName
     * @param requestId
     * @return
     */
    @PostMapping("/{userId}/repositories/{repositoryName}/requests/{requestId}/reply")
    public ResponseEntity<?> saveReply(@RequestBody HashMap<String, String> request,
                                         @PathVariable String userId, @PathVariable String repositoryName, @PathVariable Long requestId){

        HashMap<String, String> result = new HashMap<>();

        Request findRequest = requestService.findById(requestId);

        RequestReply reply = RequestReply.create(request.get("content"));

        findRequest.addReply(reply);

        RequestReply saved = requestService.replySave(reply);

        Member findMember = memberService.findByUserId(userId);

        result.put("author", saved.getCreatedBy());
        result.put("content", saved.getContent());
        result.put("name", findMember.getName());
        result.put("profile", findMember.getProfile());
        result.put("datetime", saved.getCreatedDate().toString());

        return ResponseEntity.ok(result);
    }

    /**
     * 복제지도의 변경사항 반영 요청 수락(원본지도에 변경 데이터 반영)
     *
     * @param userId
     * @param repositoryName
     * @param requestId
     * @return
     * @throws CloneNotSupportedException
     */
    @PostMapping("/{userId}/repositories/{repositoryName}/requests/{requestId}/merge")
    public ResponseEntity<?> mergeData(@PathVariable String userId, @PathVariable String repositoryName, @PathVariable Long requestId) throws CloneNotSupportedException {

        Member findMember = memberService.findByUserId(userId);

        List<SpaceResponse> target = spaceService.findOne(findMember.getId(), repositoryName);

        if (target.size() == 1 && target.get(0) != null){

            Request findRequest = requestService.findById(requestId);

            requestService.merger(target.get(0).getId(), requestId);

            alarmService.createAlarm(findRequest.getCreatedBy(), target.get(0).getId(), AlarmType.REQUEST_ACCEPTED);

            return ResponseEntity.ok("merge");
        }else{
            return ApiResult.errorMessage("존재하지 않는 지도입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 복제지도의 변경사항 반영 요청 거부
     *
     * @param userId
     * @param repositoryName
     * @param requestId
     * @return
     */
    @PostMapping("/{userId}/repositories/{repositoryName}/requests/{requestId}/denied")
    public ResponseEntity<?> deniedData(@PathVariable String userId, @PathVariable String repositoryName, @PathVariable Long requestId){

        Member findMember = memberService.findByUserId(userId);

        List<SpaceResponse> target = spaceService.findOne(findMember.getId(), repositoryName);

        if (target.size() == 1 && target.get(0) != null){

            Request findRequest = requestService.findById(requestId);

            requestService.denied(requestId);

            alarmService.createAlarm(findRequest.getCreatedBy(), target.get(0).getId(), AlarmType.REQUEST_DENIED);

            return ResponseEntity.ok("denied");

        }else{
            return ApiResult.errorMessage("존재하지 않는 지도입니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
