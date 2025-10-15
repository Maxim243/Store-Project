package com.store.exception.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExceptionType {

    NO_AVAILABLE_PRODUCT_FOUND("No available product found", 404),
    NOT_ENOUGH_PRODUCTS_FOUND("Not enough available products. Requested: %d, Available: %d", 404),
    USER_ALREADY_EXIST("User already exist", 409),
    UNAUTHORIZED_USER("Unauthorized user", 401),
    INVALID_CREDENTIALS("Invalid credentials", 401),
    NO_CART_FOUNT("No available cart fount", 404);

    final String message;

    final int statusCode;
}
