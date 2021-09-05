package com.mapgoblin.api.dto.request;

import com.mapgoblin.domain.base.RequestStatus;
import lombok.Data;

@Data
public class ValueDto {

    private String title;
    private String content;
    private RequestStatus status;
    private String createdBy;
}
