package com.mapgoblin.api.dto.request;

import com.mapgoblin.domain.Layer;
import com.mapgoblin.domain.RequestData;
import com.mapgoblin.domain.base.RequestAction;
import com.mapgoblin.domain.mapdata.MapData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChangeInfo {

    private Long id;
    private Long layerId;
    private String name;
    private LocalDateTime createdDate;
    private String geometry;
    private RequestAction action;
    private Long mapDataId;

    public static ChangeInfo createdByRequestData(RequestData requestData) {
        ChangeInfo data = new ChangeInfo();

        data.setMapDataId(requestData.getMapDataId());
        data.setLayerId(requestData.getLayerId());
        data.setGeometry(requestData.getGeometry());
        data.setName(requestData.getName());
        data.setCreatedDate(requestData.getCreateDate());
        data.setAction(requestData.getAction());

        return data;
    }

    public static ChangeInfo createdByMapData(MapData mapData, Long layerId, String geom) {
        ChangeInfo result = new ChangeInfo();
        result.setId(mapData.getId());
        result.setLayerId(layerId);
        result.setName(mapData.getName());
        result.setCreatedDate(mapData.getModifiedDate());
        result.setGeometry(geom);

        return result;
    }

    public static ChangeInfo createdByLayer(Layer layer) {
        ChangeInfo result = new ChangeInfo();
        result.setId(layer.getId());
        result.setLayerId(layer.getId());
        result.setName(layer.getName());
        result.setCreatedDate(layer.getModifiedDate());
        result.setGeometry(null);

        return result;
    }
}
