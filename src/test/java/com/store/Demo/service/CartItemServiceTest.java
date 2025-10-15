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

import java.util.Optional;

import static com.store.Demo.helper.MockHelper.*;
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
        CartEntity cartEntity = getCartWithItemsEntity();
        ProductEntity productEntity = getProductEntity();
        String userEmail = getUserEmail();

        when(cartService.findCartByUserEmail(userEmail)).thenReturn(cartEntity);
        when(productService.findProductById(productEntity.getId())).thenReturn(productEntity);

        cartItemService.addItemToCart(productEntity.getId(), 2L, userEmail);

        verify(productService).adjustProductStock(productEntity, 2L);
        verify(cartItemRepository).save(any(CartItemEntity.class));
        verify(cartService).calculateCartTotalPrice(cartEntity);
    }

    @Test
    void addItemToCart_whenExistingItem_shouldUpdateQuantity() {
        ProductEntity productEntity = getProductEntity();
        CartEntity cartEntity = getCartWithItemsEntity();
        CartItemEntity existingItem = cartEntity.getItems().getFirst();
        String userEmail = getUserEmail();

        when(cartService.findCartByUserEmail(userEmail)).thenReturn(cartEntity);
        when(productService.findProductById(productEntity.getId())).thenReturn(productEntity);
        when(cartItemRepository.save(any(CartItemEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(conversionService.convert(productEntity, ProductDTO.class))
                .thenReturn(new ProductDTO(productEntity.getId(), productEntity.getTitle(), productEntity.getAvailable(), productEntity.getPrice()));

        cartItemService.addItemToCart(productEntity.getId(), 2L, userEmail);

        verify(cartItemRepository).save(cartEntity.getItems().getFirst());
        assertThat(existingItem.getQuantity()).isEqualTo(5L);
    }

    @Test
    void removeItemFromCart_whenExistingItem_shouldRemoveItemFromCart() {
        ProductEntity productEntity = getProductEntity();
        CartEntity cartEntity = getCartWithItemsEntity();
        CartItemEntity itemToBeDeleted = cartEntity.getItems().getFirst();
        String userEmail = getUserEmail();

        when(cartService.findCartByUserEmail(userEmail)).thenReturn(cartEntity);
        when(cartItemRepository.findByCartIdAndProductId(cartEntity.getId(), productEntity.getId())).thenReturn(Optional.of(itemToBeDeleted));

        cartItemService.removeItemFromCart(productEntity.getId(), userEmail);
        verify(cartItemRepository).delete(itemToBeDeleted);
    }
}

