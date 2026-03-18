package org.example.pswrd_manager.service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class CryptoService {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;

    /**
     * Генерирует AES ключ из мастер-пароля.
     * Используем PBKDF2, чтобы сделать подбор пароля невозможным.
     */
    public SecretKey deriveKey(String masterPassword, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        // 65536 итераций — стандарт безопасности
        KeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    /**
     * Шифрует строку данных. Результат: Base64(IV + CipherText)
     */
    public String encrypt(String data, SecretKey key) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv); // Генерация уникального IV

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] cipherText = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // Склейка IV и шифр-текста в один массив, запоминание IV при расшифровке
        ByteBuffer bb = ByteBuffer.allocate(iv.length + cipherText.length);
        bb.put(iv);
        bb.put(cipherText);

        return Base64.getEncoder().encodeToString(bb.array());
    }

    public String decrypt(String encryptedBase64, SecretKey key) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(encryptedBase64);

        // извлечение вектора IV (первые 12 байт)
        ByteBuffer bb = ByteBuffer.wrap(decoded);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        bb.get(iv);

        // Остальное — это зашифрованные данные
        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText, StandardCharsets.UTF_8);
    }
}