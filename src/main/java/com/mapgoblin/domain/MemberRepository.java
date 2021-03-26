package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import com.mapgoblin.domain.base.SourceType;
import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
public class MemberRepository extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_repository_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "repository_id")
    private Repository repository;

    @Enumerated(EnumType.STRING)
    private SourceType source;
}
