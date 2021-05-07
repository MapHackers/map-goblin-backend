package com.mapgoblin.api.dto.alarm;

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
}
