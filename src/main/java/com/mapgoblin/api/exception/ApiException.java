package com.mapgoblin.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiException {

    @ExceptionHandler
    public ResponseEntity<ErrorResult> generalServerException(Exception e) {
        log.error("ApiException");
        e.printStackTrace();
        ErrorResult errorResult = new ErrorResult("server-exception", "서버 에러 발생");
        return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
