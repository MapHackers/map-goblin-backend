package com.mapgoblin.api.dto.space;

import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.LikeType;
import com.mapgoblin.domain.base.SourceType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SpaceResponse {

    private Long id;

    private Long map_id;

    private String name;

    private String thumbnail;

    private String description;

    private int likeCount;

    private int dislikeCount;

    private SourceType source;

    private String authority;

    private Long hostId;

    private String hostUserId;

    private List<String> categories = new ArrayList<>();

    private LikeType likeType;

    @QueryProjection
    public SpaceResponse(Long id, Long map_id, String name, String thumbnail,
                               String description, int likeCount, int dislikeCount, SourceType source, Long hostId){
        this.id = id;
        this.map_id = map_id;
        this.name = name;
        this.thumbnail = thumbnail;
        this.description = description;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.source = source;
        this.hostId = hostId;
    }
}
