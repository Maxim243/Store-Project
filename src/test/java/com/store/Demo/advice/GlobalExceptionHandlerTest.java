package com.store.Demo.advice;

import com.store.exception.NoCartFoundException;
import com.store.exception.NoProductAvailableException;
import com.store.exception.UnauthorizedUserException;
import com.store.exception.UserAlreadyExistsException;
import com.store.exception.advice.GlobalExceptionHandler;
import com.store.exception.type.ExceptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleNoProductAvailableException() {
        NoProductAvailableException ex = NoProductAvailableException.of(ExceptionType.NO_AVAILABLE_PRODUCT_FOUND);

        ResponseEntity<String> response = handler.handleNoProductAvailableException(ex);

        assertEquals(ExceptionType.NO_AVAILABLE_PRODUCT_FOUND.getStatusCode(), response.getStatusCode().value());
        assertEquals(ExceptionType.NO_AVAILABLE_PRODUCT_FOUND.getMessage(), response.getBody());
    }

    @Test
    void testHandleNoCartFoundException() {
        NoCartFoundException ex = NoCartFoundException.of(ExceptionType.NO_CART_FOUND);

        ResponseEntity<String> response = handler.handleNoCartFoundException(ex);

        assertEquals(ExceptionType.NO_CART_FOUND.getStatusCode(), response.getStatusCode().value());
        assertEquals(ExceptionType.NO_CART_FOUND.getMessage(), response.getBody());
    }

    @Test
    void testHandleUnauthorizedUserException() {
        UnauthorizedUserException ex = UnauthorizedUserException.of(ExceptionType.UNAUTHORIZED_USER);

        ResponseEntity<String> response = handler.handleUnauthorizedUserException(ex);

        assertEquals(ExceptionType.UNAUTHORIZED_USER.getStatusCode(), response.getStatusCode().value());
        assertEquals(ExceptionType.UNAUTHORIZED_USER.getMessage(), response.getBody());
    }

    @Test
    void testHandleUserAlreadyExistsException() {
        UserAlreadyExistsException ex = UserAlreadyExistsException.of(ExceptionType.USER_ALREADY_EXISTS);

        ResponseEntity<String> response = handler.handleUserAlreadyExistsException(ex);

        assertEquals(ExceptionType.USER_ALREADY_EXISTS.getStatusCode(), response.getStatusCode().value());
        assertEquals(ExceptionType.USER_ALREADY_EXISTS.getMessage(), response.getBody());
    }
}

