package com.urlplatform.userservice.service;

import com.urlplatform.userservice.dto.CreateUserRequest;
import com.urlplatform.userservice.dto.UpdateUserRequest;
import com.urlplatform.userservice.dto.UserResponse;
import com.urlplatform.userservice.entity.User;
import com.urlplatform.userservice.exception.DuplicateEmailException;
import com.urlplatform.userservice.exception.UserNotFoundException;
import com.urlplatform.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setName("Vikash Singh");
        sampleUser.setEmail("vikash@example.com");
        sampleUser.setCreatedAt(OffsetDateTime.now());
        sampleUser.setUpdatedAt(OffsetDateTime.now());
    }

    // ---- Create User ----

    @Test
    void createUser_Success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Vikash Singh");
        request.setEmail("vikash@example.com");

        when(userRepository.existsByEmail("vikash@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponse response = userService.createUser(request);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Vikash Singh");
        assertThat(response.getEmail()).isEqualTo("vikash@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Vikash Singh");
        request.setEmail("vikash@example.com");

        when(userRepository.existsByEmail("vikash@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("vikash@example.com");

        verify(userRepository, never()).save(any());
    }

    // ---- Get User ----

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserResponse response = userService.getUserById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("vikash@example.com");
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getAllUsers_ReturnsList() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        List<UserResponse> users = userService.getAllUsers();

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("vikash@example.com");
    }

    // ---- Update User ----

    @Test
    void updateUser_Success() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Vikash Updated");
        request.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponse response = userService.updateUser(1L, request);

        assertThat(response.getName()).isEqualTo("Vikash Updated");
        assertThat(response.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void updateUser_DuplicateEmail_ThrowsException() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Vikash");
        request.setEmail("other@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.existsByEmail("other@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void updateUser_NotFound_ThrowsException() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("X");
        request.setEmail("x@example.com");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99L, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    // ---- Delete User ----

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(UserNotFoundException.class);
    }
}
