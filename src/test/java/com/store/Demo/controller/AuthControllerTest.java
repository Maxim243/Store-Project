package com.store.Demo.controller;

import com.store.controller.AuthController;
import com.store.dto.MessageResponseDTO;
import com.store.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        }
)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @SneakyThrows
    void registerUser_shouldReturnOk() {
        Mockito.doNothing().when(userService).register(eq("test@mail.com"), eq("secret"));

        mockMvc.perform(post("/auth/register")
                        .param("email", "test@mail.com")
                        .param("password", "secret")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void loginUser_shouldReturnSessionId() {
        Mockito.when(userService.login(eq("test@mail.com"), eq("secret"), any()))
                .thenReturn("session123");

        mockMvc.perform(post("/auth/login")
                        .param("email", "test@mail.com")
                        .param("password", "secret")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value("session123"));
    }

    @Test
    @SneakyThrows
    void logoutUser_shouldReturnOkMessage() {
        MockHttpSession session = new MockHttpSession();

        Mockito.when(userService.logout(session))
                .thenReturn(new MessageResponseDTO("Logout successful"));

        mockMvc.perform(post("/auth/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }
}

