package com.store.exception;

import com.store.exception.type.ExceptionType;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

public class UnauthorizedUserException extends AuthenticationException {
    @Getter
    private final ExceptionType exceptionType;

    public UnauthorizedUserException(ExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }

    public static UnauthorizedUserException of(ExceptionType exceptionType) {
        return new UnauthorizedUserException(exceptionType);
    }
}
