package org.example.pswrd_manager.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ApiResponse {
    private final String message;
    private final long expiresIn;

    public ApiResponse(String message) {
        this.message = message;
        this.expiresIn = System.currentTimeMillis();
    }

    public ApiResponse(String message, long expiresIn) {
        this.message = message;
        this.expiresIn = expiresIn;
    }
}
