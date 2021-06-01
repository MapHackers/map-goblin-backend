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

    private String author;

    private String content;

//    private String img;

    private float rating;

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

    /**
     *
     * Create Review method
     *
     * @param mapData
     * @param content
     * @param rating
     */
    public static Review createReview(MapData mapData, String content, Float rating, String author){
        Review review = new Review();

        review.setMapData(mapData);
        review.setContent(content);
        review.setRating(rating);
        review.setAuthor(author);

        return review;
    }
}
