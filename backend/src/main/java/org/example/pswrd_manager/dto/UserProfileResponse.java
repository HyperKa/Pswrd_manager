package org.example.pswrd_manager.dto;

import lombok.Data;
import org.example.pswrd_manager.entity.User;
import java.time.format.DateTimeFormatter;

@Data
public class UserProfileResponse {
    private final String id;
    private final String username;
    private final String role;
    private final String createdAt;
    private final String lastLoginAt;
    private final boolean locked;

    public UserProfileResponse(User user) {
        this.id = user.getId().toString();
        this.username = user.getUsername();
        this.role = user.getRole().name();
        this.createdAt = user.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME);
        this.lastLoginAt = user.getLastLoginAt().format(DateTimeFormatter.ISO_DATE_TIME);
        this.locked = user.getLockedUntil().isAfter(java.time.LocalDateTime.now());
    }
}
