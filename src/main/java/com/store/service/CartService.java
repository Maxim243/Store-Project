package com.store.service;

import com.store.model.CartEntity;

public interface CartService {

    void calculateCartTotalPrice(CartEntity cartEntity);

    CartEntity findCartByUserEmail(String email);
}
