package com.store.exception;

import com.store.exception.type.ExceptionType;
import lombok.Getter;

public class NoUserFoundException extends RuntimeException {

    @Getter
    private final ExceptionType exceptionType;

    public NoUserFoundException(ExceptionType exceptionType, Object... args) {
        super(String.format(exceptionType.getMessage(), args));
        this.exceptionType = exceptionType;
    }

    public static NoUserFoundException of(ExceptionType exceptionType, Object... args) {
        return new NoUserFoundException(exceptionType, args);
    }
}
