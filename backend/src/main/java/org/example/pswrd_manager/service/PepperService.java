package org.example.pswrd_manager.service;

import jakarta.annotation.PostConstruct;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;

@Service
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class PepperService {
    @Value("${security.pepper}")
    private String pepperValue;

    private byte[] pepperBytes;

    @PostConstruct
    public void init() {
        if (pepperValue == null || pepperValue.isEmpty()) {
            throw new IllegalStateException("Pepper value must be set in .env");
        }
        this.pepperBytes = pepperValue.getBytes(StandardCharsets.UTF_8);

        // в терминал разработчика для отладки
        log.debug("Pepper values are {}", Arrays.toString(this.pepperBytes));
    }

    // НЕ ИСПОЛЬЗОВАТЬ ИЗ ДРУГИХ СЕРВИСОВ, КРОМЕ CryptoService!!!
    public String combineWithSalt(String password, byte[] salt) {
        byte[] passwordInBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] unitedPasswordInBytes = new byte[passwordInBytes.length + salt.length + pepperBytes.length];

        System.arraycopy(passwordInBytes, 0, unitedPasswordInBytes, 0, passwordInBytes.length);
        System.arraycopy(salt, 0, unitedPasswordInBytes, passwordInBytes.length, salt.length);
        System.arraycopy(pepperBytes, 0, unitedPasswordInBytes, passwordInBytes.length + salt.length, pepperBytes.length);

        log.info("Combined {} bytes (password: {}, salt: {}, pepper: {})",
                unitedPasswordInBytes.length, passwordInBytes.length, salt.length, pepperBytes.length);
        log.info("United password is: {}", unitedPasswordInBytes);
        return Base64.getEncoder().encodeToString(unitedPasswordInBytes);
    }

}
