package com.mapgoblin.exception;

public class WrongPasswordException extends IllegalArgumentException {

    public WrongPasswordException(String msg){
        super(msg);
    }
}
