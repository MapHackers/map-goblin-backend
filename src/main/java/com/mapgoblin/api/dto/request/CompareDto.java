package com.mapgoblin.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompareDto {

    private Long id;

    private String name;

    private LocalDateTime createdDate;

}
