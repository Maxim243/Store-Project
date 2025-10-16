package com.store.Demo.service;

import com.store.dto.ViewCartProductDTO;
import com.store.dto.ViewCartResponseDTO;
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

import java.util.ArrayList;
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

    @Test
    void viewCartItems_shouldReturnCorrectViewCartResponseDTO() {
        String userEmail = "test@example.com";
        Long cartId = 1L;
        Double totalPrice = 150.0;

        ProductEntity product1 = ProductEntity.builder()
                .id(1L)
                .title("T-Shirt")
                .price(100.0)
                .build();

        ProductEntity product2 = ProductEntity.builder()
                .id(2L)
                .title("Shoes")
                .price(50.0)
                .build();

        CartItemEntity item1 = CartItemEntity.builder()
                .product(product1)
                .quantity(1L)
                .build();

        CartItemEntity item2 = CartItemEntity.builder()
                .product(product2)
                .quantity(2L)
                .build();

        List<CartItemEntity> cartItems = List.of(item1, item2);

        CartEntity cartEntity = CartEntity.builder()
                .id(cartId)
                .items(new ArrayList<>(cartItems))
                .totalPrice(totalPrice)
                .build();

        when(cartRepository.findByUserEmail("test@example.com"))
                .thenReturn(Optional.of(cartEntity));

        when(cartItemRepository.findByCartId(cartEntity.getId())).thenReturn(List.of(item1, item2));


        ViewCartResponseDTO response = cartService.viewCartItems(userEmail);

        assertThat(response).isNotNull();
        assertThat(response.cartSubtotalPrice()).isEqualTo(totalPrice);
        assertThat(response.viewCartProductDTOList()).hasSize(2);

        ViewCartProductDTO first = response.viewCartProductDTOList().get(0);
        ViewCartProductDTO second = response.viewCartProductDTOList().get(1);

        assertThat(first.title()).isEqualTo("T-Shirt");
        assertThat(first.price()).isEqualTo(100.0);
        assertThat(first.ordinal()).isEqualTo(1L);

        assertThat(second.title()).isEqualTo("Shoes");
        assertThat(second.price()).isEqualTo(50.0);
        assertThat(second.ordinal()).isEqualTo(2L);

        verify(cartRepository).findByUserEmail(userEmail);
        verify(cartItemRepository).findByCartId(cartId);
    }
}

