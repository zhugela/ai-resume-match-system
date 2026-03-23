package com.liangge.resumematch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liangge.resumematch.common.ResultCode;
import com.liangge.resumematch.dto.AuthResponse;
import com.liangge.resumematch.dto.LoginRequest;
import com.liangge.resumematch.dto.RegisterRequest;
import com.liangge.resumematch.entity.User;
import com.liangge.resumematch.mapper.UserMapper;
import com.liangge.resumematch.security.JwtTokenProvider;
import com.liangge.resumematch.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userMapper.selectOne(
            new QueryWrapper<User>().eq("username", request.getUsername())
        );
        
        if (user == null) {
            throw new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage());
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException(ResultCode.PASSWORD_ERROR.getMessage());
        }
        
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setUserId(user.getId());
        return response;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // 检查用户名是否存在
        Long count = userMapper.selectCount(
            new QueryWrapper<User>().eq("username", request.getUsername())
        );
        
        if (count > 0) {
            throw new RuntimeException(ResultCode.USER_EXISTS.getMessage());
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole("USER");
        
        userMapper.insert(user);
        
        // 生成Token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setUserId(user.getId());
        return response;
    }

    @Override
    public Long getCurrentUserId() {
        // 从SecurityContext获取当前用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("用户未登录");
        }
        
        if (authentication.getPrincipal() instanceof User user) {
            return user.getId();
        }
        
        throw new RuntimeException("无效的用户信息");
    }
}
