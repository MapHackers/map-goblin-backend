package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
public class IssueReply extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "issue_reply_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "issue_id")
    private Issue issue;
}
