package com.mapgoblin.api.dto.space;

import com.mapgoblin.domain.base.LikeType;
import com.mapgoblin.domain.base.SourceType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SpaceResponse {

    private Long id;

    private Long mapId;

    private String name;

    private String thumbnail;

    private String description;

    private String oneWord;

    private int likeCount;

    private int dislikeCount;

    private SourceType source;

    private String authority;

    private Long hostId;

    private String hostUserId;

    private String hostRepoName;

    private List<String> categories = new ArrayList<>();

    private LikeType likeType;

    private List<String> owners = new ArrayList<>();

    @QueryProjection
    public SpaceResponse(Long id, Long mapId, String name, String thumbnail,
                               String description, String oneWord, int likeCount, int dislikeCount, SourceType source, Long hostId){
        this.id = id;
        this.mapId = mapId;
        this.name = name;
        this.thumbnail = thumbnail;
        this.description = description;
        this.oneWord = oneWord;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.source = source;
        this.hostId = hostId;
    }
}
