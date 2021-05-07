package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.alarm.AlarmDto;
import com.mapgoblin.api.dto.alarm.AlarmResponse;
import com.mapgoblin.domain.Alarm;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.AlarmType;
import com.mapgoblin.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlarmApi {
    private final AlarmService alarmService;

//    @GetMapping("/alarms")
//    public ResponseEntity<?> getAlarmList(){
//
//        List<Alarm> alarms = alarmService.findAll();
//        List<AlarmDto> alarmDtos = alarms.stream()
//
//    }

    @PostMapping("/alarms")
    public ResponseEntity<?> create(@RequestBody AlarmDto request){
        List<AlarmResponse> alarmList = null;

        try{
            alarmList = alarmService.save(request.getSpaceId(), request.getType());
            if(alarmList==null){ return ApiResult.errorMessage("알람 null 에러", HttpStatus.BAD_REQUEST); }
        }catch(Exception e){
            return ApiResult.errorMessage("알람 생성 에러", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(new ApiResult(alarmList));
    }
}
