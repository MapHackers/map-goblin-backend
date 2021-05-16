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
public abstract class MapData extends BaseEntity implements Cloneable {

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

    @OneToMany(mappedBy = "mapData", orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public void addReview(Review review) {
        this.reviews.add(review);
        review.setMapData(this);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        MapData mapData = (MapData) super.clone();
        mapData.id = null;
        mapData.layer = null;
        mapData.reviews = reviewListCopy(reviews, mapData);

        return mapData;
    }

    private List<Review> reviewListCopy(List<Review> list, MapData mapData){
        List<Review> result = new ArrayList<Review>();
        for (Review review : list) {
            try{
                Review clone = (Review) review.clone();
                clone.setMapData(mapData);
                result.add(clone);
            }catch (CloneNotSupportedException e){
                e.printStackTrace();
            }
        }

        return result;
    }
}
