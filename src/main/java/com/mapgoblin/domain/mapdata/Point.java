package com.mapgoblin.domain.mapdata;

import com.mapgoblin.domain.Layer;
import com.mapgoblin.domain.Marker;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@DiscriminatorValue("POINT")
@Getter
@Setter
public class Point extends MapData {
    private String geometry;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "marker_id")
    private Marker marker;

    /**
     * Create Point method
     *
     * @param layer, title, description, rating, geometry thumbnail
     * @return point
     */
    public static Point createPoint(Layer layer,String title, String descriptions, Float rating, String geometry, String thumbnail) {

        Point point = new Point();
        point.setLayer(layer);
        point.setName(title);
        point.setDescription(descriptions);
        point.setRating(rating);
        point.setReviews(null);
        point.setGeometry(geometry);
        point.setThumbnail(thumbnail);
        // marker를 설정해주어야 하나? 클라이언트에서 요청할때 어떤 이미지를 가지고 만들건지 정해서 보내주면 될 듯?
        return point;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Point point = (Point) super.clone();
        point.marker = null;

        return point;
    }
}
