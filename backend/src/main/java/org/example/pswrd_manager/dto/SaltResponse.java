package org.example.pswrd_manager.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

@Data
@Slf4j
public class SaltResponse {
    private final String salt;

    public SaltResponse(byte[] salt) {
        // this.salt = new String(salt);
        this.salt = Base64.getEncoder().encodeToString(salt);
    }
}
