package org.example.pswrd_manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoService {

    // УДАЛИТЬ В БУДУЩЕМ, только путает
    private final PepperService pepperService;

    // есть предложение добавить степень криптографии = 12, под вопросом.
    private final BCryptPasswordEncoder bCryptPasswordEncoder =  new BCryptPasswordEncoder();
    private final SecureRandom secureRandom = new SecureRandom();

    // метод генерации соли:
    public byte[] generateSalt() {
        byte[] salt = new byte[32];
        secureRandom.nextBytes(salt);
        log.debug("generateSalt length is {}", salt.length);
        return salt;
    }

    public String hashPassword(String rawPassword, byte[] salt) {
        String unitedPassword = pepperService.combineWithSalt(rawPassword, salt);

        // Без этого фикса длина пароля получается 105 байт вместо максимальных 72 байта
        // было: "Combined 105 bytes (password: 9, salt: 32, pepper: 64)"
        String preHashed = sha256(unitedPassword);

        return bCryptPasswordEncoder.encode(preHashed);
    }

    public boolean checkPassword(String rawPassword, byte[] salt, String storedHash) {
        String unitedPassword = pepperService.combineWithSalt(rawPassword, salt);
        String preHashed = sha256(unitedPassword);
        return bCryptPasswordEncoder.matches(preHashed, storedHash);
    }

    public String sha256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("CryptoService: SHA-256 error", e);
        }
    }
}
