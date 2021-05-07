package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
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
public class Space extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "space_id")
    private Long id;

    private String name;

    private String thumbnail;

    private String description;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "map_id")
    private Map map;

    private int likeCount;

    private int dislikeCount;

    @OneToMany(mappedBy = "space")
    private List<SpaceCategory> categories = new ArrayList<>();

    @OneToMany(mappedBy = "space")
    private List<Alarm> alarms = new ArrayList<>();

    /**
     * Create Space method
     *
     * @param name
     * @param thumbnail
     * @param description
     * @return
     */
    public static Space createSpace(String name, String thumbnail, String description, Map map) {
        Space space = new Space();
        space.setName(name);
        space.setThumbnail(thumbnail);
        space.setDescription(description);
        space.setMap(map);
        space.setLikeCount(0);
        space.setDislikeCount(0);

        return space;
    }
}
