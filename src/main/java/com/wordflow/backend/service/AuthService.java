package com.wordflow.backend.service;

import com.wordflow.backend.dto.request.LoginRequest;
import com.wordflow.backend.dto.request.RegisterRequest;
import com.wordflow.backend.dto.response.LoginResponse;

public interface AuthService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
