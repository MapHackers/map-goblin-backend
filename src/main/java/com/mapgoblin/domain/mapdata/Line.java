package com.mapgoblin.domain.mapdata;

import lombok.Getter;

import javax.persistence.Entity;

@Entity
@Getter
public class Line extends MapData {
    private String geometry;

    private String color;
}
