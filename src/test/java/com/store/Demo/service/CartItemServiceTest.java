package com.store.Demo.service;

import com.store.dto.ProductDTO;
import com.store.model.CartEntity;
import com.store.model.CartItemEntity;
import com.store.model.ProductEntity;
import com.store.repository.CartItemRepository;
import com.store.service.CartService;
import com.store.service.ProductService;
import com.store.service.implementation.CartItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @InjectMocks
    private CartItemServiceImpl cartItemService;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @Mock
    private ConversionService conversionService;

    @Test
    void addItemToCart_whenNewItem_shouldSaveNewCartItem() {
        CartEntity cartEntity = CartEntity.builder()
                .id(1L)
                .items(new ArrayList<>())
                .build();

        ProductEntity productEntity = ProductEntity.builder()
                .id(10L)
                .price(50.0)
                .available(100L)
                .build();

        when(cartService.findCartByUserEmail("test@mail.com")).thenReturn(cartEntity);
        when(productService.findProductById(10L)).thenReturn(productEntity);

        cartItemService.addItemToCart(10L, 2L, "test@mail.com");

        verify(productService).adjustProductStock(productEntity, 2L);
        verify(cartItemRepository).save(any(CartItemEntity.class));
        verify(cartService).calculateCartTotalPrice(cartEntity);
    }

    @Test
    void addItemToCart_whenExistingItem_shouldUpdateQuantity() {
        ProductEntity productEntity = ProductEntity.builder()
                .id(10L)
                .price(50.0)
                .available(100L)
                .title("test")
                .build();

        CartEntity cartEntity = CartEntity.builder()
                .id(1L)
                .items(new ArrayList<>())
                .build();

        CartItemEntity existingItem = CartItemEntity.builder()
                .cart(cartEntity)
                .product(productEntity)
                .quantity(3L)
                .build();
        cartEntity.getItems().add(existingItem);

        when(cartService.findCartByUserEmail("test@mail.com")).thenReturn(cartEntity);
        when(productService.findProductById(10L)).thenReturn(productEntity);
        when(cartItemRepository.save(any(CartItemEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(conversionService.convert(productEntity, ProductDTO.class))
                .thenReturn(new ProductDTO(productEntity.getId(), productEntity.getTitle(), productEntity.getAvailable(), productEntity.getPrice()));

        cartItemService.addItemToCart(10L, 2L, "test@mail.com");

        verify(cartItemRepository).save(existingItem);
        assertThat(existingItem.getQuantity()).isEqualTo(5L);
    }


}

