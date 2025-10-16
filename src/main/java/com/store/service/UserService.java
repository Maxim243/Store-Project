package com.store.service;

import com.store.dto.MessageResponseDTO;
import com.store.model.UserEntity;
import jakarta.servlet.http.HttpSession;

public interface UserService {

    boolean existsUserByEmail(String email);

    void register(String email, String password);

    String login(String email, String password, HttpSession session);

    MessageResponseDTO logout(HttpSession httpSession);

    UserEntity findByEmail(String userEmail);

}
