package com.mapgoblin.api.dto.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RequestDataDto {

    private List<ValueDto> values;
    private List<ChangeInfo> added;
    private List<ChangeInfo> modified;
    private List<ChangeInfo> delete;
    private List<ChangeInfo> layer;
    private List<ReplyDto> replies;

    public RequestDataDto() {
        values = new ArrayList<>();
        added = new ArrayList<>();
        modified = new ArrayList<>();
        delete = new ArrayList<>();
        layer = new ArrayList<>();
        replies = new ArrayList<>();
    }

    public boolean isEmpty() {
        return added.isEmpty() && modified.isEmpty() && delete.isEmpty() && layer.isEmpty();
    }
}
