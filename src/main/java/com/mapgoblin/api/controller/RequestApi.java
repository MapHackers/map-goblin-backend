package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.request.*;
import com.mapgoblin.api.dto.space.SpaceResponse;
import com.mapgoblin.domain.*;
import com.mapgoblin.domain.base.AlarmType;
import com.mapgoblin.domain.base.RequestStatus;
import com.mapgoblin.domain.base.SourceType;
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

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RequestApi {

    private final RequestService requestService;
    private final SpaceService spaceService;
    private final MemberService memberService;
    private final AlarmService alarmService;
    private final MemberSpaceService memberSpaceService;

    /**
     * 요청사항 리스트 조회
     *
     * @param userId
     * @param spaceName
     * @param status
     * @param pageable
     * @return
     */
    @GetMapping("/{userId}/spaces/{spaceName}/requests")
    public ResponseEntity<?> getRequestList(@PathVariable String userId, @PathVariable String spaceName, @RequestParam String status,
                                            @PageableDefault(size = 8, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Member findMember = memberService.findByUserId(userId);

        SpaceResponse spaceResponse = spaceService.findOne(findMember.getId(), spaceName);

        if (spaceResponse != null){
            Space space = spaceService.findById(spaceResponse.getId());

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
     * @param spaceName
     * @param requestId
     * @return
     */
    @GetMapping("/{userId}/spaces/{spaceName}/requests/{requestId}")
    public ResponseEntity<?> getRequestInfo(@PathVariable String userId, @PathVariable String spaceName, @PathVariable Long requestId){

        RequestDataDto result = requestService.findRequestInfoById(requestId);

        if(result != null){
            return ResponseEntity.ok(result);
        }else{
            return ApiResult.errorMessage("해당 요청이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 요청사항 생성
     *
     * @param requestData
     * @param userId
     * @param spaceName
     * @return
     */
    @PostMapping("/{userId}/spaces/{spaceName}/requests")
    public ResponseEntity<?> create(@RequestBody RequestDataDto requestData,
                                    @PathVariable String userId, @PathVariable String spaceName) {

        Member findMember = memberService.findByUserId(userId);

        SpaceResponse spaceResponse = spaceService.findOne(findMember.getId(), spaceName);

        if (spaceResponse != null){
            List<ValueDto> values = requestData.getValues();
            Space findSpace = spaceService.findById(spaceResponse.getId());

            Request request = Request.create(values.get(0).getTitle(), values.get(1).getContent(), findSpace);

            HashMap<String, Long> result = new HashMap<>();

            result.put("requestId", requestService.save(request, requestData));

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
     * @param spaceName
     * @param member
     * @return
     */
    @GetMapping("/{userId}/spaces/{spaceName}/compare")
    public ResponseEntity<?> compareOriginClone(@PathVariable String userId, @PathVariable String spaceName,
                                                @AuthenticationPrincipal Member member) {

        HashMap<String, List<CompareDto>> result;

        Member findMember = memberService.findByUserId(userId);

        SpaceResponse originSpaceResponse = spaceService.findOne(findMember.getId(), spaceName);

        if (originSpaceResponse != null){

            SpaceResponse clonedSpaceResponse = spaceService.findOne(member.getId(), originSpaceResponse.getId());

            if(clonedSpaceResponse != null){

                result = requestService.compareMapData(originSpaceResponse.getId(), clonedSpaceResponse.getId());

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
     * @param spaceName
     * @return
     * @throws CloneNotSupportedException
     */
    @PostMapping("/{userId}/spaces/{spaceName}/pull")
    public ResponseEntity<?> pullData(@PathVariable String userId, @PathVariable String spaceName) throws CloneNotSupportedException {

        return ResponseEntity.ok("");
    }

    /**
     * 원본지도 변경 데이터 감지
     *
     * @param userId
     * @param spaceName
     * @param member
     * @return
     */
    @GetMapping("/{userId}/spaces/{spaceName}/pull/compare")
    public ResponseEntity<?> comparePullData(@PathVariable String userId, @PathVariable String spaceName,
                                             @AuthenticationPrincipal Member member){

        HashMap<String, List<CompareDto>> result;

        Member findMember = memberService.findByUserId(userId);

        SpaceResponse spaceResponse = spaceService.findOne(findMember.getId(), spaceName);

        if (spaceResponse != null && spaceResponse.getSource() == SourceType.CLONE){

            List<MemberSpace> spacesOfMember = memberSpaceService.findSpacesOfMember(member);
            Space clonedSpace = spaceService.findById(spaceResponse.getId());
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

            result = requestService.comparePullData(spaceResponse.getHostId(), clonedSpace);

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
     * @param spaceName
     * @param requestId
     * @return
     */
    @PostMapping("/{userId}/spaces/{spaceName}/requests/{requestId}/reply")
    public ResponseEntity<?> saveReply(@RequestBody HashMap<String, String> request,
                                       @PathVariable String userId, @PathVariable String spaceName,
                                       @PathVariable Long requestId){

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
     * @param spaceName
     * @param requestId
     * @return
     * @throws CloneNotSupportedException
     */
    @PostMapping("/{userId}/spaces/{spaceName}/requests/{requestId}/merge")
    public ResponseEntity<?> mergeData(@PathVariable String userId, @PathVariable String spaceName, @PathVariable Long requestId) throws CloneNotSupportedException {

        Member findMember = memberService.findByUserId(userId);

        SpaceResponse spaceResponse = spaceService.findOne(findMember.getId(), spaceName);

        if (spaceResponse != null){

            Request findRequest = requestService.findById(requestId);

            requestService.merge(spaceResponse.getId(), requestId);

            alarmService.createAlarm(findRequest.getCreatedBy(), spaceResponse.getId(), AlarmType.REQUEST_ACCEPTED);

            return ResponseEntity.ok("merge");
        }else{
            return ApiResult.errorMessage("존재하지 않는 지도입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 복제지도의 변경사항 반영 요청 거부
     *
     * @param userId
     * @param spaceName
     * @param requestId
     * @return
     */
    @PostMapping("/{userId}/spaces/{spaceName}/requests/{requestId}/denied")
    public ResponseEntity<?> deniedData(@PathVariable String userId, @PathVariable String spaceName, @PathVariable Long requestId){

        Member findMember = memberService.findByUserId(userId);

        SpaceResponse spaceResponse = spaceService.findOne(findMember.getId(), spaceName);

        if (spaceResponse != null){

            Request findRequest = requestService.findById(requestId);

            requestService.denied(requestId);

            alarmService.createAlarm(findRequest.getCreatedBy(), spaceResponse.getId(), AlarmType.REQUEST_DENIED);

            return ResponseEntity.ok("denied");

        }else{
            return ApiResult.errorMessage("존재하지 않는 지도입니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
