package com.store.Demo.controller;


import com.store.controller.CartController;
import com.store.dto.ProductDTO;
import com.store.dto.ViewCartProductDTO;
import com.store.dto.ViewCartResponseDTO;
import com.store.service.CartItemService;
import com.store.service.CartService;
import com.store.utils.CustomUserDetails;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
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

        CustomUserDetails mockUser = new CustomUserDetails(
                1L,
                "test@example.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(cartItemService.addItemToCart(1L, 2L, mockUser.getUsername())).thenReturn(productDTO);

        String json = """
                {
                  "id": 1,
                  "quantity": 2
                }
                """;

        mockMvc.perform(post("/cart/items")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                mockUser, mockUser.getPassword(), mockUser.getAuthorities()
                        )))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @SneakyThrows
    void viewCartItems_shouldReturnOK() {
        ViewCartProductDTO productDTO = ViewCartProductDTO.builder()
                .id(1L)
                .title("test")
                .price(15.0)
                .quantity(4L)
                .build();

        ViewCartResponseDTO mockResponse = ViewCartResponseDTO.builder()
                .viewCartProductDTOList(List.of(productDTO))
                .cartSubtotalPrice(33.0)
                .build();

        CustomUserDetails mockUser = new CustomUserDetails(
                1L,
                "test@example.com",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(cartService.viewCartItems(anyString())).thenReturn(mockResponse);

        mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                mockUser, mockUser.getPassword(), mockUser.getAuthorities()
                        )))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartSubtotalPrice").value(33.0))
                .andExpect(jsonPath("$.viewCartProductDTOList[0].id").value(1L))
                .andExpect(jsonPath("$.viewCartProductDTOList[0].title").value("test"))
                .andExpect(jsonPath("$.viewCartProductDTOList[0].price").value(15.0))
                .andExpect(jsonPath("$.viewCartProductDTOList[0].quantity").value(4L));
    }

    @Test
    @SneakyThrows
    public void deleteItemFromCart_shouldReturnOK() {
        Mockito.doNothing().when(cartItemService).removeItemFromCart(1L, "test");

        CustomUserDetails mockUser = new CustomUserDetails(1L, "test@example.com", "password", List.of());

        mockMvc.perform(delete("/cart/item/{id}", 1L)
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                mockUser, mockUser.getPassword(), mockUser.getAuthorities()
                        )))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
