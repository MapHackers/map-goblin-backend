package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import com.mapgoblin.domain.base.RequestStatus;
import com.mapgoblin.domain.base.RequestTag;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
public class Request extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "request_id")
    private Long id;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Enumerated(EnumType.STRING)
    private RequestTag tag;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @OneToMany(mappedBy = "request")
    private List<RequestData> requestDataList = new ArrayList<>();

    public static Request create(String title, String content, Space space){
        Request request = new Request();
        request.setTitle(title);
        request.setContent(content);
        request.setStatus(RequestStatus.WAITING);
        request.setTag(RequestTag.REQUEST);
        request.setSpace(space);

        return request;
    }

    public void addRequestData(RequestData requestData) {
        requestDataList.add(requestData);
        requestData.setRequest(this);
    }
}
