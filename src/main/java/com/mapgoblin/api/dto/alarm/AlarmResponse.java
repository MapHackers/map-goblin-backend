package com.mapgoblin.api.dto.alarm;

import com.mapgoblin.domain.Alarm;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmResponse {

    private Long id;

    private String dstMemberName;

    private String srcMemberName;

    private AlarmType alarmType;

    private String spaceName;

    private String thumbnail;

    private boolean isRead;

    private LocalDateTime date;

    public AlarmResponse(Alarm alarm, String srcName){
        this.id = alarm.getId();
        this.dstMemberName = alarm.getDstMember().getName();
        this.srcMemberName = srcName;
        this.alarmType = alarm.getAlarmType();
        this.spaceName = alarm.getDstSpace().getName();
        this.thumbnail = alarm.getDstSpace().getThumbnail();
        this.isRead = alarm.isRead();
        this.date = alarm.getCreatedDate();
    }
}
