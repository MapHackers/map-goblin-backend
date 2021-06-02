package com.mapgoblin.api.dto.request;

import com.mapgoblin.domain.Space;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

public class RequestDto {
    private Long id;

    private String title;

    private String content;

    private String createdBy;

    private LocalDateTime createdDate;
}
