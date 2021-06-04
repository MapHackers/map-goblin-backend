package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.request.CompareDto;
import com.mapgoblin.api.dto.request.RequestDto;
import com.mapgoblin.api.dto.space.SpaceResponse;
import com.mapgoblin.domain.Layer;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.Request;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.mapdata.MapData;
import com.mapgoblin.service.MemberService;
import com.mapgoblin.service.RequestService;
import com.mapgoblin.service.SpaceService;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
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

    @GetMapping("/{userId}/repositories/{repositoryName}/requests")
    public ResponseEntity<?> getRequestList(@PathVariable String userId, @PathVariable String repositoryName,
                                            @PageableDefault(size = 8, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {


        Member findMember = memberService.findByUserId(userId);

        List<SpaceResponse> target = spaceService.findOne(findMember.getId(), repositoryName);

        if (target.get(0) != null && target.size() == 1){
            Space space = spaceService.findById(target.get(0).getId());

            Page<RequestDto> result = requestService.findRequestsOfSpace(space, pageable);

            return ResponseEntity.ok(result);

        }else{
            return ApiResult.errorMessage("존재하지 않는 지도입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{userId}/repositories/{repositoryName}/requests")
    public ResponseEntity<?> create(@RequestBody HashMap<String, List<HashMap<String, String>>> request,
                                    @PathVariable String userId, @PathVariable String repositoryName) {

        System.out.println("리퀘스트 생성");
        System.out.println(request.toString());

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
            return ApiResult.errorMessage("해당 레포지토리가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
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
                        CompareDto compareDto = new CompareDto();
                        compareDto.setId(clonedLayer.getId());
                        compareDto.setLayerId(clonedLayer.getId());
                        compareDto.setName(clonedLayer.getName());
                        compareDto.setCreatedDate(clonedLayer.getModifiedDate());

                        createdLayer.add(compareDto);
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
                                    CompareDto compareDto = new CompareDto();
                                    compareDto.setId(mapData.getId());
                                    compareDto.setLayerId(hostLayer.getId());
                                    compareDto.setName(mapData.getName());
                                    compareDto.setCreatedDate(mapData.getModifiedDate());

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

                            cloneGeom.forEach((s, mapData) -> {
                                if(!hostGeom.containsKey(s)){
                                    CompareDto compareDto = new CompareDto();
                                    compareDto.setId(mapData.getId());
                                    compareDto.setLayerId(hostLayer.getId());
                                    compareDto.setName(mapData.getName());
                                    compareDto.setCreatedDate(mapData.getModifiedDate());

                                    addedList.add(compareDto);

                                    geoms.add(s);
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
                                    CompareDto compareDto = new CompareDto();
                                    compareDto.setId(mapData.getId());
                                    compareDto.setLayerId(hostLayer.getId());
                                    compareDto.setName(mapData.getName());
                                    compareDto.setCreatedDate(mapData.getModifiedDate());

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

        }else{
            return ApiResult.errorMessage("존재하지 않는 지도입니다.", HttpStatus.BAD_REQUEST);
        }
    }

}
