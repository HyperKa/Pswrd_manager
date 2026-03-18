package org.example.pswrd_manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class LoginRequest {

    @NotBlank(message = "логин обязателен")
    private final String username;

    @NotBlank(message = "Мастер-пароль обязателен")
    private final String masterPassword;
}
