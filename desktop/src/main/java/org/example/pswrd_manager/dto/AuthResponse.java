package org.example.pswrd_manager.dto;

import org.example.pswrd_manager.entity.User;

public class AuthResponse {
    private String token;
    private String username;
    private User.Role role;
    private long expiresIn;

    public AuthResponse() {
    }

    public AuthResponse(String token, String username, User.Role role, long expiresIn) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
}
