package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Map extends BaseEntity implements Cloneable {

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
        System.out.println("*************************************************");
        System.out.println(this.layers);
        System.out.println("*************************************************");
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Map map = (Map) super.clone();
        map.id = null;
        map.layers = layerListCopy(layers, map);

        return map;
    }

    private List<Layer> layerListCopy(List<Layer> list, Map map){
        List<Layer> result = new ArrayList<Layer>();
        for (Layer layer : list) {
            try{
                Layer clone = (Layer) layer.clone();
                clone.setMap(map);
                result.add(clone);
            }catch (CloneNotSupportedException e){
                e.printStackTrace();
            }
        }

        return result;
    }
}
