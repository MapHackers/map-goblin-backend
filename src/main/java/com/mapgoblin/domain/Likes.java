package com.mapgoblin.domain;

import com.mapgoblin.domain.base.LikeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Likes {

    @Id
    @GeneratedValue
    @Column(name = "likes_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    private Member member;

    @ManyToOne(fetch = LAZY)
    private Space space;

    @Enumerated(EnumType.STRING)
    private LikeType type;

    public static Likes create(LikeType type){
        Likes like = new Likes();
        like.type = type;

        return like;
    }
}
