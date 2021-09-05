package com.mapgoblin.api.dto.request;

import com.mapgoblin.domain.RequestData;
import com.mapgoblin.domain.base.RequestAction;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChangeInfo {

    private Long id;
    private Long layerId;
    private String name;
    private LocalDateTime createDate;
    private String geometry;
    private RequestAction action;
    private Long mapDataId;

    public static ChangeInfo byRequestData(RequestData requestData) {
        ChangeInfo data = new ChangeInfo();

        data.setMapDataId(requestData.getMapDataId());
        data.setLayerId(requestData.getLayerId());
        data.setGeometry(requestData.getGeometry());
        data.setName(requestData.getName());
        data.setCreateDate(requestData.getCreateDate());
        data.setAction(requestData.getAction());

        return data;
    }
}
