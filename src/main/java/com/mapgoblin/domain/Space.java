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
public class Space extends BaseEntity implements Cloneable {

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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "host_id")
    private Space host;

    @OneToMany(mappedBy = "space")
    private List<Likes> likes = new ArrayList<>();

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

    public void addCategory(SpaceCategory spaceCategory){
        this.categories.add(spaceCategory);
        spaceCategory.setSpace(this);
    }

    public void addLikes(Likes like){
        likes.add(like);
        like.setSpace(this);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Space space = (Space) super.clone();
        space.id = null;
        space.map = (Map)map.clone();
        space.categories = categoryListCopy(categories, space);
        space.likes = null;

        return space;
    }

    private List<SpaceCategory> categoryListCopy(List<SpaceCategory> list, Space space){
        List<SpaceCategory> result = new ArrayList<SpaceCategory>();
        for (SpaceCategory spaceCategory : list) {
            try{
                SpaceCategory clone = (SpaceCategory) spaceCategory.clone();
                clone.setSpace(space);
                result.add(clone);
            }catch (CloneNotSupportedException e){
                e.printStackTrace();
            }
        }

        return result;
    }
}
