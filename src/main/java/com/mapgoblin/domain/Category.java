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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    @OneToMany(mappedBy = "category")
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
        category.parent = (Category) parent.clone();
        category.child = childListCopy(child);
        category.spaces = null;

        return category;
    }

    private List<Category> childListCopy(List<Category> list){
        List<Category> result = new ArrayList<Category>();
        for (Category category : list) {
            try{
                result.add((Category) category.clone());
            }catch (CloneNotSupportedException e){
                e.printStackTrace();
            }
        }

        return result;
    }
}
