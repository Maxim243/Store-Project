package com.store.controller;

import com.store.dto.AddItemDTO;
import com.store.dto.ProductDTO;
import com.store.service.CartItemService;
import com.store.utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart/items")
@RequiredArgsConstructor
public class CartController {

    private final CartItemService cartItemService;

    @PostMapping
    public ProductDTO addProductItemToCart(@RequestBody AddItemDTO addItemDTO,
                                                           @AuthenticationPrincipal CustomUserDetails user) {
        return cartItemService.addItemToCart(addItemDTO.id(), addItemDTO.quantity(), user.getUsername());
    }
}
