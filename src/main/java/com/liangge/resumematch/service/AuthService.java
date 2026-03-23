package com.liangge.resumematch.service;

import com.liangge.resumematch.dto.AuthResponse;
import com.liangge.resumematch.dto.LoginRequest;
import com.liangge.resumematch.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    Long getCurrentUserId();
}
