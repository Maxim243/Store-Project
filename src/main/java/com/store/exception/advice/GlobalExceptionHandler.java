package com.store.exception.advice;

import com.store.exception.NoCartFoundException;
import com.store.exception.NoProductAvailableException;
import com.store.exception.UnauthorizedUserException;
import com.store.exception.UserAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoProductAvailableException.class)
    public ResponseEntity<String> handleNoProductAvailableException(NoProductAvailableException ex) {
        return ResponseEntity.status(ex.getExceptionType().getStatusCode()).body(ex.getMessage());
    }

    @ExceptionHandler(NoCartFoundException.class)
    public ResponseEntity<String> handleNoCartFoundException(NoCartFoundException ex) {
        return ResponseEntity.status(ex.getExceptionType().getStatusCode()).body(ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<String> handleUnauthorizedUserException(UnauthorizedUserException ex) {
        return ResponseEntity.status(ex.getExceptionType().getStatusCode()).body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity.status(ex.getExceptionType().getStatusCode()).body(ex.getMessage());
    }
}
