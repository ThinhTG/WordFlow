package com.wordflow.backend.service;

import com.wordflow.backend.domain.Level;
import com.wordflow.backend.domain.User;
import com.wordflow.backend.dto.request.LoginRequest;
import com.wordflow.backend.dto.request.RegisterRequest;
import com.wordflow.backend.dto.response.LoginResponse;
import com.wordflow.backend.exception.AppException;
import com.wordflow.backend.repository.UserRepository;
import com.wordflow.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {

        // 1. Check email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already exists");
        }

        // 2. Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 3. Create user
        User user = User.builder()
                .email(request.getEmail())
                .password(hashedPassword)
                .level(Level.BEGINNER)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 4. Save DB
        userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        // 1. Find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("Invalid email or password"));

        // 2. Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException("Invalid email or password");
        }

        // 3. Generate JWT
        String token = jwtService.generateToken(user.getEmail());

        // 4. Return response
        return LoginResponse.builder()
                .token(token)
                .email(user.getEmail())
                .level(user.getLevel().name())
                .build();
    }
}

