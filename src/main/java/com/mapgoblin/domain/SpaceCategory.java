package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SpaceCategory extends BaseEntity implements Cloneable {

    @Id
    @GeneratedValue
    @Column(name = "space_category_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        SpaceCategory spaceCategory = (SpaceCategory) super.clone();
        spaceCategory.id = null;
        spaceCategory.space = null;
        spaceCategory.category = (Category) category.clone();

        return spaceCategory;
    }
}
