package com.store.service.implementation;

import com.store.exception.NoCartFoundException;
import com.store.exception.type.ExceptionType;
import com.store.model.CartEntity;
import com.store.repository.CartItemRepository;
import com.store.repository.CartRepository;
import com.store.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    @Override
    public void calculateCartTotalPrice(CartEntity cartEntity) {
        Double cartTotalPrice = cartItemRepository.findByCartId(cartEntity.getId())
                .stream()
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice())
                .sum();
        cartEntity.setTotalPrice(cartTotalPrice);
        cartRepository.save(cartEntity);
    }

    @Override
    public CartEntity findCartByUserEmail(String userEmail) {
        return cartRepository.findByUserEmail(userEmail).orElseThrow(() -> NoCartFoundException.of(ExceptionType.NO_CART_FOUNT));
    }
}
