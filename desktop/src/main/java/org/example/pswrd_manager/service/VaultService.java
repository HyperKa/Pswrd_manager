package org.example.pswrd_manager.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pswrd_manager.dto.VaultDto;
import org.example.pswrd_manager.dto.VaultEntry;

import javax.crypto.SecretKey;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class VaultService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String API_URL = "http://localhost:8080/api/auth/vault";
    private final CryptoService cryptoService = new CryptoService();

    public List<VaultEntry> loadVault(String token, SecretKey key) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + token)
                .GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // Читаем ответ как наш новый VaultDto
            VaultDto dto = mapper.readValue(response.body(), VaultDto.class);
            String encryptedBase64 = dto.getEncryptedVault();

            if (encryptedBase64 == null || encryptedBase64.equals("{}") || encryptedBase64.isEmpty()) {
                return new java.util.ArrayList<>();
            }

            String decryptedJson = new CryptoService().decrypt(encryptedBase64, key);
            return mapper.readValue(decryptedJson, new TypeReference<List<VaultEntry>>(){});
        }
        return new java.util.ArrayList<>();
    }

    public void save(List<VaultEntry> entries, String token, SecretKey key) throws Exception {
        // сериализация: List -> JSON
        String json = mapper.writeValueAsString(entries);
        System.out.println("DEBUG: Список паролей в JSON: " + json);
        String encryptedPayload =  cryptoService.encrypt(json, key);

        // шифрование типа: JSON -> AES-GCM -> Base64
        VaultDto dto = new VaultDto(encryptedPayload);
        String requestBody = mapper.writeValueAsString(dto);
        System.out.println("DEBUG: Итоговый JSON для сервера: " + requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Сервер вернул ошибку: " + response.body());
        }
    }


}
