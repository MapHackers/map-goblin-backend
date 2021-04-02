package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import com.mapgoblin.domain.mapdata.MapData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Layer extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "layer_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "map_id")
    private Map map;

    @OneToMany(mappedBy = "layer")
    private List<MapData> mapDataList = new ArrayList<>();

    /**
     * Create Layer method
     *
     * @param name
     * @return
     */
    public static Layer createLayer(String name) {
        Layer layer = new Layer();
        layer.setName(name);

        return layer;
    }

    public void addMapData(MapData mapData) {
        this.mapDataList.add(mapData);
        mapData.setLayer(this);
    }
}
