package com.mapgoblin.domain.mapdata;

import com.mapgoblin.domain.Layer;
import com.mapgoblin.domain.Review;
import com.mapgoblin.domain.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;
import static javax.persistence.InheritanceType.JOINED;

@Entity
@Inheritance(strategy = JOINED)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MapData extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "map_data_id")
    private Long id;

    private String name;

    private String description;

    private Float rating;

    private String thumbnail;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "layer_id")
    private Layer layer;

    @OneToMany(mappedBy = "mapData")
    private List<Review> reviews = new ArrayList<>();

    public void addReview(Review review) {
        this.reviews.add(review);
        review.setMapData(this);
    }

}
