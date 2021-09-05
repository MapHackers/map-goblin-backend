package com.mapgoblin.api.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReplyDto {

    private String author;
    private String content;
    private String name;
    private String profile;
    private LocalDateTime datetime;
}
