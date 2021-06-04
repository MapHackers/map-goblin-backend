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
public class Layer extends BaseEntity implements Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "layer_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "map_id")
    private Map map;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "host_id")
    private Layer host;

    @OneToMany(mappedBy = "layer", orphanRemoval = true)
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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Layer layer = (Layer) super.clone();
        layer.id = null;
        layer.map = null;
        layer.mapDataList = mapDataListCopy(mapDataList, layer);

        return layer;
    }

    private List<MapData> mapDataListCopy(List<MapData> list, Layer layer){
        List<MapData> result = new ArrayList<MapData>();

        list.forEach(mapData -> {

        });
        for (MapData mapData : list) {
            System.out.println(mapData.getClass());
            try{
                MapData clone = (MapData) mapData.clone();
                clone.setLayer(layer);
                result.add(clone);
            }catch (CloneNotSupportedException e){
                e.printStackTrace();
            }
        }

        return result;
    }
}
