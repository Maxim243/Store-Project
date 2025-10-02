package com.store.service;

import com.store.dto.ProductDTO;
import com.store.model.CartItemEntity;

import java.util.Set;

public interface CartItemService {

    ProductDTO addItemToCart(Long productId, Long quantity, String userEmail);

    void removeItemFromCart(Long productId, String userEmail);

    void removeAllItemsFromCartByIds(Set<Long> productIds, String userEmail);

    CartItemEntity findByCartIdAndProductId(Long cartId, Long productId);
}