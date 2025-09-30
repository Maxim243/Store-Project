package com.store.controller;

import com.store.dto.MessageResponseDTO;
import com.store.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestParam String email,
                                             @RequestParam String password) {
        userService.register(email, password);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public Map<String, String> loginUser(@RequestParam String email,
                                                         @RequestParam String password, HttpSession session) {
        String sessionId = userService.login(email, password, session);
        return Collections.singletonMap("sessionId", sessionId);
    }

    @PostMapping("/logout")
    public MessageResponseDTO logoutUser(HttpSession session) {
        return userService.logout(session);
    }
}
