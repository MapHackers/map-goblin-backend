package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import com.mapgoblin.domain.base.RequestStatus;
import com.mapgoblin.domain.base.RequestTag;
import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
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
}
