package com.mapgoblin.domain.mapdata;

import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("POLYGON")
@Getter
public class Polygon extends MapData {
    private String geometry;

    private String color;

    @Override
    public Object clone() throws CloneNotSupportedException {
        Polygon polygon = (Polygon) super.clone();

        return polygon;
    }
}
