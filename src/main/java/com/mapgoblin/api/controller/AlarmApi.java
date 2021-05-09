package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.alarm.AlarmDto;
import com.mapgoblin.api.dto.alarm.AlarmIdDto;
import com.mapgoblin.api.dto.alarm.AlarmResponse;
import com.mapgoblin.domain.Alarm;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.AlarmType;
import com.mapgoblin.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AlarmApi {
    private final AlarmService alarmService;

    @GetMapping("/{userId}/alarms")
    public ResponseEntity<?> getAlarmList(@PathVariable String userId){

        List<Alarm> alarms = alarmService.findAlarmsByMemberId(userId);
        if (alarms == null) {
            return ApiResult.errorMessage("alarm 없음", HttpStatus.BAD_REQUEST);
        }
        List<AlarmResponse> collect = alarms.stream()
                .map(alarm -> new AlarmResponse(alarm))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResult(collect));
    }

    @PostMapping("/alarms")
    public ResponseEntity<?> setReadByAlarmId(@RequestBody AlarmIdDto request){
        if(!alarmService.setAlarmRead(request.getAlarmId())){
            return ApiResult.errorMessage("존재하지 않는 알람 에러", HttpStatus.BAD_REQUEST);
        }
        else{
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }


//    @PostMapping("/alarms")
//    public ResponseEntity<?> create(@RequestBody AlarmDto request){
//        List<AlarmResponse> alarmList = null;
//
//        try{
//            alarmList = alarmService.save(request.getSpaceId(), request.getType());
//            if(alarmList==null){ return ApiResult.errorMessage("알맞지 않은 request Ex) spaceId", HttpStatus.BAD_REQUEST); }
//        }catch(Exception e){
//            return ApiResult.errorMessage("알람 생성 에러", HttpStatus.BAD_REQUEST);
//        }
//
//        return ResponseEntity.ok(new ApiResult(alarmList));
//    }
}
