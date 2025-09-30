package com.store.exception;

import com.store.exception.type.ExceptionType;
import lombok.Getter;

public class UserAlreadyExistsException extends RuntimeException {

    @Getter
    private final ExceptionType exceptionType;

    public UserAlreadyExistsException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

    public static UserAlreadyExistsException of(ExceptionType exceptionType) {
        return new UserAlreadyExistsException(exceptionType);
    }
}
