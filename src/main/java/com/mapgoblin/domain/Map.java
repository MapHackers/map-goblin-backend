package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Map extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "map_id")
    private Long id;

    @OneToMany(mappedBy = "map")
    private List<Layer> layers = new ArrayList<>();
}
