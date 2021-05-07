package com.mapgoblin.api.dto.alarm;

import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    private boolean isRead;
}
