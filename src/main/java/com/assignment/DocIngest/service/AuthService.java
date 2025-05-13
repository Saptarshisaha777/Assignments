package com.assignment.DocIngest.service;

import com.assignment.DocIngest.dto.AuthRequest;
import com.assignment.DocIngest.dto.AuthResponse;
import com.assignment.DocIngest.dto.RegisterRequest;
import com.assignment.DocIngest.entity.User;
import com.assignment.DocIngest.repository.UserRepository;
import com.assignment.DocIngest.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtTokenProvider tokenProvider;

    public void register(RegisterRequest req) {
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(req.getRole());
        userRepo.save(user);
    }

    public AuthResponse login(AuthRequest req) {
        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = tokenProvider.createToken(user.getUsername(), user.getRole());
        return new AuthResponse(token);
    }


    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
}
