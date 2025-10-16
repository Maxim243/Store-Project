package com.store.Demo.service;

import com.store.exception.NoUserFoundException;
import com.store.exception.UserAlreadyExistsException;
import com.store.exception.type.ExceptionType;
import com.store.model.CartEntity;
import com.store.model.UserEntity;
import com.store.repository.CartRepository;
import com.store.repository.UserRepository;
import com.store.service.implementation.UserServiceImpl;
import com.store.utils.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpSession httpSession;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .id(1L)
                .email("test@mail.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    void register_shouldSaveUserAndCart_whenEmailDoesNotExist() {
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        userService.register("test@mail.com", "password");

        verify(userRepository).save(any(UserEntity.class));
        verify(cartRepository).save(any(CartEntity.class));
    }

    @Test
    void register_shouldThrowException_whenUserAlreadyExists() {
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(userEntity));

        assertThatThrownBy(() -> userService.register("test@mail.com", "password"))
                .isInstanceOf(UserAlreadyExistsException.class)
                .extracting("exceptionType")
                .isEqualTo(ExceptionType.USER_ALREADY_EXISTS);

        verify(userRepository, never()).save(any());
        verify(cartRepository, never()).save(any());
    }


    @Test
    void login_shouldSetSecurityContextInSession() {
        CustomUserDetails userDetails = new CustomUserDetails(userEntity.getId(), userEntity.getEmail(), userEntity.getPassword(), null);
        when(userDetailsService.loadUserByUsername("test@mail.com")).thenReturn(userDetails);

        MockHttpSession session = new MockHttpSession();

        String sessionId = userService.login("test@mail.com", "password", session);

        SecurityContext context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
        assertThat(context).isNotNull();
        assertThat(context.getAuthentication().getName()).isEqualTo("test@mail.com");
        assertThat(sessionId).isEqualTo(session.getId());
    }


    @Test
    void logout_shouldInvalidateSession() {
        userService.logout(httpSession);
        verify(httpSession).invalidate();
    }

    @Test
    void findByEmail_shouldReturnUser_whenUserExists() {
        String email = "test@example.com";
        UserEntity mockUser = new UserEntity();
        mockUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserEntity user = userService.findByEmail(email);

        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(email);
    }

    @Test
    void findByEmail_shouldThrowException_whenUserDoesNotExist() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail(email))
                .isInstanceOf(NoUserFoundException.class)
                .hasMessageContaining(ExceptionType.NO_USER_FOUND.getMessage());
    }
}

