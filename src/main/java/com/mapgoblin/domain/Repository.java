package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
public class Repository extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "repository_id")
    private Long id;

    private String name;

    private String thumbnail;

    private String description;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "map_id")
    private Map map;

    private int likeCount;

    private int dislikeCount;

    @OneToMany(mappedBy = "repository")
    private List<RepositoryCategory> categories = new ArrayList<>();

}
