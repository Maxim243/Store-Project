package com.store.Demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.controller.OrderController;
import com.store.dto.CheckOutOrderDTO;
import com.store.dto.OrderResponseDTO;
import com.store.dto.ProductOrderDTO;
import com.store.model.PaymentMethod;
import com.store.service.OrderService;
import com.store.utils.CustomUserDetails;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    @SneakyThrows
    void createOrder_shouldReturnOrderResponse() {

        CheckOutOrderDTO checkOutOrderDTO = CheckOutOrderDTO.builder()
                .orderItems(List.of(
                        ProductOrderDTO.builder().productId(1L).quantity(2L).build(),
                        ProductOrderDTO.builder().productId(2L).quantity(1L).build()
                ))
                .paymentMethod(PaymentMethod.CARD)
                .shippingAddress("123 Street")
                .build();

        OrderResponseDTO mockResponse = OrderResponseDTO.builder()
                .orderId(100L)
                .totalPrice(50.0)
                .products(List.of(
                        ProductOrderDTO.builder().productId(1L).quantity(2L).build(),
                        ProductOrderDTO.builder().productId(2L).quantity(1L).build()
                ))
                .orderCreatedDateTime(LocalDateTime.now())
                .build();

        when(orderService.addOrder(any(CheckOutOrderDTO.class), eq("test@example.com")))
                .thenReturn(mockResponse);

        CustomUserDetails mockUser = new CustomUserDetails(
                1L,
                "test@example.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(checkOutOrderDTO);

        mockMvc.perform(post("/order")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                mockUser, mockUser.getPassword(), mockUser.getAuthorities()
                        )))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(100L))
                .andExpect(jsonPath("$.totalPrice").value(50.0))
                .andExpect(jsonPath("$.products[0].productId").value(1L))
                .andExpect(jsonPath("$.products[0].quantity").value(2L))
                .andExpect(jsonPath("$.products[1].productId").value(2L))
                .andExpect(jsonPath("$.products[1].quantity").value(1L))
                .andDo(print());
    }
}