package com.store.Demo.service;

import com.store.exception.UnauthorizedUserException;
import com.store.exception.type.ExceptionType;
import com.store.model.UserEntity;
import com.store.repository.UserRepository;
import com.store.service.implementation.CustomUserDetailsServiceImpl;
import com.store.utils.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceImplTest {

    @InjectMocks
    private CustomUserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    void loadUserByUsername_whenUserExists_shouldReturnCustomUserDetails() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email("test@mail.com")
                .password("hashedPassword")
                .build();

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(userEntity));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@mail.com");

        assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
        assertThat(userDetails.getUsername()).isEqualTo("test@mail.com");
        assertThat(userDetails.getPassword()).isEqualTo("hashedPassword");
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void loadUserByUsername_whenUserDoesNotExist_shouldThrowUnauthorizedUserException() {
        when(userRepository.findByEmail("missing@mail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("missing@mail.com"))
                .isInstanceOf(UnauthorizedUserException.class)
                .extracting("exceptionType")
                .isEqualTo(ExceptionType.UNAUTHORIZED_USER);
    }
}

