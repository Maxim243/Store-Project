package com.store.controller;

import com.store.dto.AddItemDTO;
import com.store.dto.ProductDTO;
import com.store.dto.ViewCartResponseDTO;
import com.store.service.CartItemService;
import com.store.service.CartService;
import com.store.utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartItemService cartItemService;

    private final CartService cartService;

    @PostMapping("/items")
    public ProductDTO addProductItemToCart(@RequestBody AddItemDTO addItemDTO,
                                           @AuthenticationPrincipal CustomUserDetails user) {
        return cartItemService.addItemToCart(addItemDTO.id(), addItemDTO.quantity(), user.getUsername());
    }

    @GetMapping
    public ViewCartResponseDTO viewCartItems(@AuthenticationPrincipal CustomUserDetails user) {
        return cartService.viewCartItems(user.getUsername());
    }
}
