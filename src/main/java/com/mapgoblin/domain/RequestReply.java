package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
public class RequestReply extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "request_reply_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "request_id")
    private Request request;

    public static RequestReply create(String content) {
        RequestReply reply = new RequestReply();
        reply.setContent(content);

        return reply;
    }
}
