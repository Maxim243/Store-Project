package com.store.Demo.service;

import com.store.exception.NoCartFoundException;
import com.store.exception.type.ExceptionType;
import com.store.model.CartEntity;
import com.store.model.CartItemEntity;
import com.store.model.ProductEntity;
import com.store.repository.CartItemRepository;
import com.store.repository.CartRepository;
import com.store.service.implementation.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Test
    void calculateCartTotalPrice_shouldSumItemsAndSave() {
        CartEntity cart = CartEntity.builder().id(1L).build();
        ProductEntity product1 = ProductEntity.builder().price(50.0).build();
        ProductEntity product2 = ProductEntity.builder().price(30.0).build();

        CartItemEntity item1 = CartItemEntity.builder().cart(cart).product(product1).quantity(2L).build();
        CartItemEntity item2 = CartItemEntity.builder().cart(cart).product(product2).quantity(3L).build();

        when(cartItemRepository.findByCartId(cart.getId())).thenReturn(List.of(item1, item2));

        cartService.calculateCartTotalPrice(cart);

        assertThat(cart.getTotalPrice()).isEqualTo(item1.getQuantity() * product1.getPrice()
                + item2.getQuantity() * product2.getPrice());
        verify(cartRepository).save(cart);
    }

    @Test
    void findCartByUserEmail_whenCartExists_shouldReturnCart() {
        CartEntity cart = CartEntity.builder().id(1L).build();
        when(cartRepository.findByUserEmail("test@mail.com")).thenReturn(Optional.of(cart));

        CartEntity result = cartService.findCartByUserEmail("test@mail.com");

        assertThat(result).isEqualTo(cart);
    }

    @Test
    void findCartByUserEmail_whenCartDoesNotExist_shouldThrowException() {
        when(cartRepository.findByUserEmail("missing@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.findCartByUserEmail("missing@mail.com"))
                .isInstanceOf(NoCartFoundException.class)
                .extracting("exceptionType")
                .isEqualTo(ExceptionType.NO_CART_FOUND);
    }
}

