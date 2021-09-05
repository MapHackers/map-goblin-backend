package com.mapgoblin.api.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class RequestDataDto {

    private List<ValueDto> values;
    private List<ChangeInfo> added;
    private List<ChangeInfo> modified;
    private List<ChangeInfo> delete;
    private List<ChangeInfo> layer;
    private List<ReplyDto> replies;
}
