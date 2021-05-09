package com.mapgoblin.domain.mapdata;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("LINE")
@Getter
@Setter
public class Line extends MapData {
    private String geometry;

    private String color;

    @Override
    public Object clone() throws CloneNotSupportedException {
        Line line = (Line) super.clone();

        return line;
    }
}
