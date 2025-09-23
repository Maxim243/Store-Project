package com.store.service.implementation;

import com.store.dto.ProductDTO;
import com.store.model.CartEntity;
import com.store.model.CartItemEntity;
import com.store.model.ProductEntity;
import com.store.repository.CartItemRepository;
import com.store.service.CartItemService;
import com.store.service.CartService;
import com.store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    private final CartService cartService;

    private final ProductService productService;

    private final ConversionService conversionService;

    @Transactional
    @Override
    public ProductDTO addItemToCart(Long productId, Long productQuantity, String userEmail) {
        CartEntity cartEntity = cartService.findCartByUserEmail(userEmail);
        ProductEntity productEntity = productService.findProductById(productId);

        productService.adjustProductStock(productEntity, productQuantity);

        saveOrUpdateCartItemEntity(productId, productQuantity, cartEntity, productEntity);

        cartService.calculateCartTotalPrice(cartEntity);
        return conversionService.convert(productEntity, ProductDTO.class);
    }

    private void saveOrUpdateCartItemEntity(Long productId, Long productQuantity, CartEntity cartEntity, ProductEntity productEntity) {
        cartEntity.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresentOrElse(item -> {
                    item.setQuantity(item.getQuantity() + productQuantity);
                    cartItemRepository.save(item);
                }, () -> {
                    CartItemEntity cartItemEntity = CartItemEntity.builder()
                            .cart(cartEntity)
                            .product(productEntity)
                            .quantity(productQuantity)
                            .build();
                    cartItemRepository.save(cartItemEntity);
                });
    }

}
