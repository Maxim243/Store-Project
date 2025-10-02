package com.store.controller;

import com.store.dto.CheckOutOrderDTO;
import com.store.dto.OrderResponseDTO;
import com.store.service.OrderService;
import com.store.utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponseDTO createOrder(@RequestBody CheckOutOrderDTO checkOutOrderDTO
            , @AuthenticationPrincipal CustomUserDetails user) {
        return orderService.addOrder(checkOutOrderDTO, user.getUsername());
    }
}
