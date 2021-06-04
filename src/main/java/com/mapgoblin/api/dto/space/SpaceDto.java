package com.mapgoblin.api.dto.space;

import com.mapgoblin.domain.Map;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.SpaceCategory;
import com.mapgoblin.domain.base.LikeType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.LAZY;

@Data
public class SpaceDto {

    private Long id;

    private String name;

    private String thumbnail;

    private String description;

    private int likeCount;

    private int dislikeCount;

    private int visitCount;

    private String ownerId;

    private String userName;

    private String userProfile;

    private List<SpaceCategoryDto> categories;

    private LikeType likeType;

    private LocalDateTime date;

    public SpaceDto(Space space) {
        this.id = space.getId();
        this.name = space.getName();
        this.thumbnail = space.getThumbnail();
        this.description = space.getDescription();
        this.likeCount = space.getLikeCount();
        this.dislikeCount = space.getDislikeCount();
        this.visitCount = space.getVisitCount();
        this.categories = space.getCategories()
                .stream()
                .map(spaceCategory -> new SpaceCategoryDto(spaceCategory))
                .collect(Collectors.toList());
    }
    public SpaceDto(Space space, Member member) {
        this.id = space.getId();
        this.name = space.getName();
        this.thumbnail = space.getThumbnail();
        this.description = space.getDescription();
        this.likeCount = space.getLikeCount();
        this.dislikeCount = space.getDislikeCount();
        this.visitCount = space.getVisitCount();
        this.categories = space.getCategories()
                .stream()
                .map(spaceCategory -> new SpaceCategoryDto(spaceCategory))
                .collect(Collectors.toList());
        this.ownerId = member.getUserId();
        this.userName = member.getName();
        this.userProfile = member.getProfile();
        this.date = space.getCreatedDate();
    }
}
