package com.store.service.implementation;

import com.store.dto.ViewCartProductDTO;
import com.store.dto.ViewCartResponseDTO;
import com.store.exception.NoCartFoundException;
import com.store.exception.type.ExceptionType;
import com.store.model.CartEntity;
import com.store.model.CartItemEntity;
import com.store.model.ProductEntity;
import com.store.repository.CartItemRepository;
import com.store.repository.CartRepository;
import com.store.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

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
        return cartRepository.findByUserEmail(userEmail).orElseThrow(() -> NoCartFoundException.of(ExceptionType.NO_CART_FOUND));
    }

    @Override
    public ViewCartResponseDTO viewCartItems(String email) {
        CartEntity cartEntity = findCartByUserEmail(email);

        Double cartPriceTotal = cartEntity.getTotalPrice();
        List<CartItemEntity> cartItems = cartItemRepository.findByCartId(cartEntity.getId());

        return ViewCartResponseDTO.builder()
                .viewCartProductDTOList(buildViewCartProductDTOList(cartItems))
                .cartSubtotalPrice(cartPriceTotal)
                .build();

    }

    private List<ViewCartProductDTO> buildViewCartProductDTOList(List<CartItemEntity> cartItems) {
        return IntStream.range(0, cartItems.size())
                .mapToObj(i -> {
                    CartItemEntity cartItem = cartItems.get(i);
                    ProductEntity product = cartItem.getProduct();

                    return ViewCartProductDTO.builder()
                            .id(product.getId())
                            .title(product.getTitle())
                            .price(product.getPrice())
                            .quantity(cartItem.getQuantity())
                            .ordinal(i + 1L)
                            .build();
                })
                .toList();
    }

}
