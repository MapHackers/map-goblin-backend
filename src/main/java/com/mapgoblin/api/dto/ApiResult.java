package com.mapgoblin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class ApiResult<T> {
    private T data;

    public static ResponseEntity<?> errorMessage(String s, HttpStatus status) {
        HashMap<String, String> result = new HashMap<String, String>();

        result.put("message", s);

        return new ResponseEntity<>(result, status);
    }
}
