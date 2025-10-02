package com.store.Demo.helper;

import com.store.model.CartEntity;
import com.store.model.CartItemEntity;
import com.store.model.ProductEntity;

import java.util.List;

public class MockHelper {

    public static ProductEntity getProductEntity() {
        return ProductEntity.builder()
                .id(10L)
                .price(50.0)
                .available(100L)
                .title("test")
                .build();
    }

    public static CartEntity getCartWithItemsEntity() {
        CartItemEntity cartItemEntity = CartItemEntity.builder()
                .product(getProductEntity())
                .quantity(3L)
                .build();

        CartEntity cartEntity = CartEntity.builder()
                .id(1L)
                .items(List.of(cartItemEntity))
                .build();

        cartItemEntity.setCart(cartEntity);
        return cartEntity;
    }

    public static CartItemEntity getCartItemEntity() {
        return CartItemEntity.builder()
                .id(1L)
                .product(getProductEntity())
                .quantity(3L)
                .cart(getCartWithItemsEntity())
                .build();
    }

    public static String getUserEmail() {
        return "test@mail.com";
    }

}
