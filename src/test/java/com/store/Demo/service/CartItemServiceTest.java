package com.store.Demo.service;

import com.store.dto.ProductDTO;
import com.store.exception.NoCartItemFoundException;
import com.store.exception.NoProductAvailableException;
import com.store.exception.type.ExceptionType;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
                .availableQuantity(100L)
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
                .availableQuantity(100L)
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
                .thenReturn(new ProductDTO(productEntity.getId(), productEntity.getTitle(), productEntity.getAvailableQuantity(), productEntity.getPrice()));

        cartItemService.addItemToCart(10L, 2L, "test@mail.com");

        verify(cartItemRepository).save(existingItem);
        assertThat(existingItem.getQuantity()).isEqualTo(5L);
    }

    @Test
    void removeItemFromCart_shouldDeleteCartItem_whenExists() {
        String userEmail = "test@example.com";
        Long productId = 1L;

        CartEntity cartEntity = CartEntity.builder().id(10L).build();
        CartItemEntity cartItemEntity = new CartItemEntity();

        when(cartService.findCartByUserEmail(userEmail)).thenReturn(cartEntity);
        when(cartItemRepository.findByCartIdAndProductId(cartEntity.getId(), productId))
                .thenReturn(Optional.of(cartItemEntity));

        cartItemService.removeItemFromCart(productId, userEmail);

        verify(cartItemRepository).delete(cartItemEntity);
    }

    @Test
    void removeItemFromCart_shouldThrowException_whenCartItemNotFound() {
        String userEmail = "test@example.com";
        Long productId = 1L;

        CartEntity cartEntity = CartEntity.builder().id(10L).build();

        when(cartService.findCartByUserEmail(userEmail)).thenReturn(cartEntity);
        when(cartItemRepository.findByCartIdAndProductId(cartEntity.getId(), productId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartItemService.removeItemFromCart(productId, userEmail))
                .isInstanceOf(NoProductAvailableException.class)
                .hasMessageContaining(ExceptionType.NO_AVAILABLE_PRODUCT_FOUND.getMessage());

        verify(cartItemRepository, never()).delete(any());
    }

    @Test
    void removeAllItemsFromCartByIds_shouldRemoveMatchingItems() {
        String userEmail = "test@example.com";
        Long productId1 = 1L;
        Long productId2 = 2L;
        Set<Long> productIdsToRemove = Set.of(productId1);

        ProductEntity product1 = ProductEntity.builder().id(productId1).build();
        ProductEntity product2 = ProductEntity.builder().id(productId2).build();

        CartItemEntity item1 = CartItemEntity.builder().product(product1).build();
        CartItemEntity item2 = CartItemEntity.builder().product(product2).build();

        CartEntity cart = CartEntity.builder()
                .items(new ArrayList<>(List.of(item1, item2)))
                .build();

        when(cartService.findCartByUserEmail(userEmail)).thenReturn(cart);

        cartItemService.removeAllItemsFromCartByIds(productIdsToRemove, userEmail);

        assertThat(cart.getItems()).containsExactly(item2);
        verify(cartService).findCartByUserEmail(userEmail);
    }

    @Test
    void findByCartIdAndProductId_shouldReturnCartItem_whenFound() {
        Long cartId = 1L;
        Long productId = 2L;
        CartItemEntity cartItem = new CartItemEntity();

        when(cartItemRepository.findByCartIdAndProductId(cartId, productId))
                .thenReturn(Optional.of(cartItem));

        CartItemEntity result = cartItemService.findByCartIdAndProductId(cartId, productId);

        assertThat(result).isEqualTo(cartItem);
        verify(cartItemRepository).findByCartIdAndProductId(cartId, productId);
    }

    @Test
    void findByCartIdAndProductId_shouldThrowException_whenNotFound() {
        Long cartId = 1L;
        Long productId = 2L;

        when(cartItemRepository.findByCartIdAndProductId(cartId, productId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartItemService.findByCartIdAndProductId(cartId, productId))
                .isInstanceOf(NoCartItemFoundException.class)
                .hasMessageContaining("No available cart item found");

        verify(cartItemRepository).findByCartIdAndProductId(cartId, productId);
    }
}

