package com.store.exception;

import com.store.exception.type.ExceptionType;
import lombok.Getter;

public class NoCartFoundException extends RuntimeException{

    @Getter
    private final ExceptionType exceptionType;

    public NoCartFoundException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

    public static NoCartFoundException of(ExceptionType exceptionType) {
        return new NoCartFoundException(exceptionType);
    }
}
