package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
public class RepositoryCategory extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "repository_category_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "repository_id")
    private Repository repository;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
}
