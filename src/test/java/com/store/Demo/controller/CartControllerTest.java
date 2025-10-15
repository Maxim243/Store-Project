package com.store.Demo.controller;


import com.store.controller.CartController;
import com.store.dto.ProductDTO;
import com.store.dto.ViewCartProductDTO;
import com.store.dto.ViewCartResponseDTO;
import com.store.service.CartItemService;
import com.store.service.CartService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CartController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        })
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartItemService cartItemService;

    @MockitoBean
    private CartService cartService;


    @Test
    @SneakyThrows
    public void addProduct_shouldReturnOK() {
        ProductDTO productDTO = ProductDTO.builder()
                .id(1L)
                .title("test")
                .available(3L)
                .build();

        when(cartItemService.addItemToCart(1L, 2L, "user@gmail.com")).thenReturn(productDTO);

        String json = """
                {
                  "id": 1,
                  "quantity": 2
                }
                """;

        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @SneakyThrows
    public void viewCartItems_shouldReturnOK() {
        ViewCartResponseDTO mockViewCartResponseDTO = ViewCartResponseDTO
                .builder()
                .viewCartProductDTOList(List.of(new ViewCartProductDTO(1L, "test", 15.0, 4L, 7L)))
                .cartSubtotalPrice(33.0)
                .build();

        when(cartService.viewCartItems(anyString())).thenReturn(mockViewCartResponseDTO);

        mockMvc.perform(get("/cart")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.viewCartProductDTOList[0].id").value(1L))
                .andExpect(status().isOk());

    }
}
