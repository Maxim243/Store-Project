package com.store.service.implementation;

import com.store.dto.MessageResponseDTO;
import com.store.exception.NoUserFoundException;
import com.store.exception.UserAlreadyExistsException;
import com.store.exception.type.ExceptionType;
import com.store.model.CartEntity;
import com.store.model.UserEntity;
import com.store.repository.CartRepository;
import com.store.repository.UserRepository;
import com.store.service.UserService;
import com.store.utils.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final CartRepository cartRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsService userDetailsService;

    @Override
    public boolean existsUserByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public void register(String email, String password) {
        log.info("Attempting to register new user with email: {}", email);
        if (existsUserByEmail(email)) {
            log.warn("Registration failed: User with email {} already exists", email);
            throw new UserAlreadyExistsException(ExceptionType.USER_ALREADY_EXIST);
        }
        UserEntity newUser = UserEntity.builder().email(email).password(passwordEncoder.encode(password)).build();
        userRepository.save(newUser);

        CartEntity cartEntityTobeSaved = CartEntity.builder().user(newUser).totalPrice(0.0).build();
        cartRepository.save(cartEntityTobeSaved);

        log.info("User with email {} registered successfully", email);
    }

    public String login(String email, String rawPassword, HttpSession session) {
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, rawPassword, userDetails.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

        return session.getId();
    }

    public MessageResponseDTO logout(HttpSession session) {
        session.invalidate();
        return MessageResponseDTO
                .builder()
                .message("Logout successfully")
                .build();
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> NoUserFoundException.of(ExceptionType.NO_USER_FOUND));
    }
}
