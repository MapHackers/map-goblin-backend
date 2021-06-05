package com.mapgoblin.domain;

import com.mapgoblin.domain.base.RequestAction;
import com.mapgoblin.domain.base.RequestStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
public class RequestData {

    @Id
    @GeneratedValue
    @Column(name = "request_data_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "request_id")
    private Request request;

    private Long mapDataId;

    private Long layerId;

    private String name;

    private LocalDateTime createDate;

    private String geometry;

    @Enumerated(EnumType.STRING)
    private RequestAction action;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public static RequestData create(Long mapDataId, Long layerId, String name, LocalDateTime createDate, String geometry, RequestAction action){
        RequestData requestData = new RequestData();
        requestData.setMapDataId(mapDataId);
        requestData.setLayerId(layerId);
        requestData.setName(name);
        requestData.setCreateDate(createDate);
        requestData.setGeometry(geometry);
        requestData.setAction(action);
        requestData.setStatus(RequestStatus.WAITING);

        return requestData;
    }
}
