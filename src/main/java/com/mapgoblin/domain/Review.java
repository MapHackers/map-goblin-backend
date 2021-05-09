package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import com.mapgoblin.domain.mapdata.MapData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity implements Cloneable {

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        Review review = (Review) super.clone();
        review.id = null;
        review.mapData = null;

        return review;
    }
}
