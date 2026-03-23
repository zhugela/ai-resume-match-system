package com.liangge.resumematch.controller;

import com.liangge.resumematch.common.Result;
import com.liangge.resumematch.dto.AuthResponse;
import com.liangge.resumematch.dto.LoginRequest;
import com.liangge.resumematch.dto.RegisterRequest;
import com.liangge.resumematch.entity.User;
import com.liangge.resumematch.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/register")
    public Result<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @GetMapping("/me")
    public Result<AuthResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return Result.error(401, "用户未登录");
        }
        
        if (authentication.getPrincipal() instanceof User user) {
            AuthResponse response = new AuthResponse();
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setRole(user.getRole());
            return Result.success(response);
        }
        
        return Result.error(401, "无效的用户信息");
    }
}
