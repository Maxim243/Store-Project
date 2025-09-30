package com.store.exception;

import com.store.exception.type.ExceptionType;
import lombok.Getter;

public class NoProductAvailableException extends RuntimeException {
    @Getter
    private final ExceptionType exceptionType;

    public NoProductAvailableException(ExceptionType exceptionType, Object... args) {
        super(String.format(exceptionType.getMessage(), args));
        this.exceptionType = exceptionType;
    }

    public static NoProductAvailableException of(ExceptionType exceptionType, Object... args) {
        return new NoProductAvailableException(exceptionType, args);
    }
}

