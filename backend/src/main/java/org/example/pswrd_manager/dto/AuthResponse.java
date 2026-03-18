package org.example.pswrd_manager.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.pswrd_manager.entity.User;

@Data
@RequiredArgsConstructor
public class AuthResponse {
    private final String token;
    private final String username;
    private final User.Role role;
    private final long expiresIn;
}
