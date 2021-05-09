package com.mapgoblin.api.dto.alarm;

import com.mapgoblin.domain.Alarm;
import com.mapgoblin.domain.base.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmDto {

    private Long spaceId;

    private AlarmType type;

    public AlarmDto(Alarm alarm){
        this.spaceId = alarm.getDstSpace().getId();
        this.type = alarm.getAlarmType();
    }
}
