package com.mapgoblin.api.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateSpaceRequest {
    private String name;
    private String thumbnail;
    private String description;
    private List<String> categories;
    private String oneWord;
}
