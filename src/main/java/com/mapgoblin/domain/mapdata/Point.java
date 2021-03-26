package com.mapgoblin.domain.mapdata;

import com.mapgoblin.domain.Marker;
import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
public class Point extends MapData {
    private String geometry;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "marker_id")
    private Marker marker;
}
