package com.urlplatform.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urlplatform.userservice.dto.CreateUserRequest;
import com.urlplatform.userservice.dto.UserResponse;
import com.urlplatform.userservice.exception.DuplicateEmailException;
import com.urlplatform.userservice.exception.UserNotFoundException;
import com.urlplatform.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserResponse sampleResponse() {
        return new UserResponse(1L, "Vikash Singh", "vikash@example.com",
                OffsetDateTime.now(), OffsetDateTime.now());
    }

    @Test
    void createUser_Returns201() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Vikash Singh");
        request.setEmail("vikash@example.com");

        when(userService.createUser(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Vikash Singh"))
                .andExpect(jsonPath("$.email").value("vikash@example.com"));
    }

    @Test
    void createUser_InvalidRequest_Returns400() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("");
        request.setEmail("not-an-email");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createUser_DuplicateEmail_Returns409() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Vikash Singh");
        request.setEmail("vikash@example.com");

        when(userService.createUser(any())).thenThrow(new DuplicateEmailException("vikash@example.com"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void getAllUsers_Returns200() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Vikash Singh"));
    }

    @Test
    void getUserById_Returns200() throws Exception {
        when(userService.getUserById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserById_NotFound_Returns404() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteUser_Returns204() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
