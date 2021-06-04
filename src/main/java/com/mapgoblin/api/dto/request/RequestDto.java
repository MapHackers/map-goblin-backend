package com.mapgoblin.api.dto.request;

import com.mapgoblin.domain.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {
    private Long id;

    private String title;

    private String content;

    private String createdBy;

    private LocalDateTime createdDate;

    public RequestDto(Request request){
        this.id = request.getId();
        this.title = request.getTitle();
        this.content = request.getContent();
        this.createdBy = request.getCreatedBy();
        this.createdDate = request.getCreatedDate();
    }
}
