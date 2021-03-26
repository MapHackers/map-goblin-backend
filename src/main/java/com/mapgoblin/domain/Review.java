package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import com.mapgoblin.domain.mapdata.MapData;
import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
public class Review extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    private String title;

    private String content;

    private String img;

    private int rating;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "map_data_id")
    private MapData mapData;
}
