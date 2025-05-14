package com.assignment.DocIngest;

import com.assignment.DocIngest.controller.AuthController;
import com.assignment.DocIngest.dto.AuthRequest;
import com.assignment.DocIngest.dto.AuthResponse;
import com.assignment.DocIngest.dto.RegisterRequest;
import com.assignment.DocIngest.entity.Role;
import com.assignment.DocIngest.entity.User;
import com.assignment.DocIngest.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private Role role = Role.ADMIN;

    @Test
    void register() {
        RegisterRequest requestdummy = new RegisterRequest();
        requestdummy.setUsername("test");
        requestdummy.setPassword("pass");
        requestdummy.setRole(role);
        final RegisterRequest request = requestdummy;
        when(authService.register(request)).thenReturn("User registered");

        final ResponseEntity<?> response = authController.register(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered", response.getBody());
    }

    @Test
    void login() {

        final AuthRequest request = new AuthRequest();
        request.setUsername("test");
        request.setPassword("pass");
        when(authService.login(request)).thenReturn(new AuthResponse("token"));

        final ResponseEntity<AuthResponse> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("token", response.getBody().getToken());
    }

    @Test
    void getAllUsers() {
        User user1 = new User(); // Create user instances
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");

        when(authService.getAllUsers()).thenReturn(List.of(user1, user2));

        final ResponseEntity<?> response = authController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(user1, user2), response.getBody());
    }
}