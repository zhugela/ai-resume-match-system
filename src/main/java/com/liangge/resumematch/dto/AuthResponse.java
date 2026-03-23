package com.liangge.resumematch.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private Long userId;
}
