package com.mapgoblin.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiException {

    @ExceptionHandler
    public ResponseEntity<ErrorResult> generalServerException(Exception e) {
        ErrorResult errorResult = new ErrorResult("server-exception", "서버 에러 발생");
        return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
