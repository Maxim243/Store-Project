package com.store.service;

import com.store.dto.ProductDTO;

public interface CartItemService {

    ProductDTO addItemToCart(Long productId, Long quantity, String userEmail);
}