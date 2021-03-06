package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.alarm.AlarmIdDto;
import com.mapgoblin.api.dto.alarm.AlarmResponse;
import com.mapgoblin.domain.Alarm;
import com.mapgoblin.domain.Member;
import com.mapgoblin.service.AlarmService;
import com.mapgoblin.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlarmApi {
    private final AlarmService alarmService;
    private final MemberService memberService;

    /**
     *
     * @param userId
     * @return
     */
    @GetMapping("/{userId}/alarms")
    public ResponseEntity<?> getAlarmList(@PathVariable String userId){

        List<Alarm> alarms = alarmService.findAlarmsByMemberId(userId);
        if (alarms == null) {
            return ApiResult.errorMessage("alarm 없음", HttpStatus.BAD_REQUEST);
        }
        List<AlarmResponse> alarmResponseList = new ArrayList<AlarmResponse>();
        for (Alarm alarm:alarms
             ) {
            Member member = memberService.findByUserId(alarm.getCreatedBy());
            if (member != null) {
                alarmResponseList.add(new AlarmResponse(alarm, member.getName()));
            }
        }

        return ResponseEntity.ok(new ApiResult(alarmResponseList));
    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("/alarms")
    public ResponseEntity<?> setReadByAlarmId(@RequestBody AlarmIdDto request){
        if(!alarmService.setAlarmRead(request.getAlarmId())){
            return ApiResult.errorMessage("존재하지 않는 알람 에러", HttpStatus.BAD_REQUEST);
        }
        else{
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PostMapping("/{userId}/alarms")
    public ResponseEntity<?> setAllRead(@PathVariable String userId){
        if(!alarmService.setAllAlarmRead(userId)){
            return ApiResult.errorMessage("모든 알람 읽기 처리 에러", HttpStatus.BAD_REQUEST);
        }
        else{
            return new ResponseEntity<>(HttpStatus.OK);
        }

    }
}
