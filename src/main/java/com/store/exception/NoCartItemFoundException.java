package com.store.exception;

import com.store.exception.type.ExceptionType;
import lombok.Getter;

public class NoCartItemFoundException extends RuntimeException {

    @Getter
    private final ExceptionType exceptionType;

    public NoCartItemFoundException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

    public static NoCartItemFoundException of(ExceptionType exceptionType) {
        return new NoCartItemFoundException(exceptionType);
    }
}
