package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
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
@NoArgsConstructor
public class Category extends BaseEntity implements Cloneable {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category", orphanRemoval = true)
    private List<SpaceCategory> spaces = new ArrayList<>();

    public static Category createCategory(String name){
        Category category = new Category();
        category.setName(name);

        return category;
    }

    public void addSpaceCategory(SpaceCategory spaceCategory){
        this.spaces.add(spaceCategory);
        spaceCategory.setCategory(this);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Category category = (Category) super.clone();
        category.id = null;
        category.spaces = null;

        return category;
    }
}
