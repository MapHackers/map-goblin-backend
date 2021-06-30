package com.mapgoblin.api.dto.request;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class RequestDataDto {

    private List<HashMap<String, String>> values;
    private List<HashMap<String, String>> added;
    private List<HashMap<String, String>> modified;
    private List<HashMap<String, String>> delete;
    private List<HashMap<String, String>> layer;
}
