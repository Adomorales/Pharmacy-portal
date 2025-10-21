package com.pharmacy.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class LoginResponseDto {

    @NotNull
    private Long userId;

    @NotBlank
    private String token;

    private String username;

    @NotNull
    @Future
    private LocalDateTime expiresAt;

    private List<String> roles;

    public LoginResponseDto() {}

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public LocalDateTime getExpiresAt() { return expiresAt; }

    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public List<String> getRoles() { return roles; }

    public void setRoles(List<String> roles) { this.roles = roles; }
}
