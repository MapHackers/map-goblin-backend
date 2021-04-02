package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Map extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "map_id")
    private Long id;

    @OneToMany(mappedBy = "map")
    private List<Layer> layers = new ArrayList<>();

    /**
     * Create Map method
     *
     * @return
     */
    public static Map createMap() {
        Map map = new Map();

        return map;
    }

    public void addLayer(Layer layer) {
        this.layers.add(layer);
        layer.setMap(this);
    }
}
