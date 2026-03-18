package org.example.pswrd_manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class RegisterRequest {

    @NotBlank(message = "логин обязателен")
    @Size(max = 255, message = "логин слишком длинный")
    private final String username;

    @NotBlank(message = "Мастер-пароль обязателен")
    @Size(min = 8, max = 128, message = "Мастер-пароль должен содержать от 8 до 128 символов")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Пароль должен содержать минимум: одну цифру, одну заглавную букву, одну строчную букву и один спецсимвол (@#$%^&+=!)"
    )
    private final String masterPassword;

    @NotBlank(message = "Зашифрованное хранилище обязательно")
    private final String encryptedVault;

}
