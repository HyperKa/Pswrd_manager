package org.example.pswrd_manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.pswrd_manager.dto.LoginRequest;
import org.example.pswrd_manager.dto.AuthResponse;
import org.example.pswrd_manager.dto.RegisterRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper(); // для превращения объектов в JSON и обратно
    private final String BASE_URL = "http://localhost:8080/api/auth";

    public AuthResponse login(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);

        String jsonBody = objectMapper.writeValueAsString(loginRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        // Отправка и получение ответа
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), AuthResponse.class);
        } else {
            throw new RuntimeException("Login failed with status: " + response.statusCode());
        }
    }


    public AuthResponse register(String username, String password, String encryptedVault) throws Exception {
        // Используем DTO регистрации
        RegisterRequest registerRequest = new RegisterRequest(username, password, encryptedVault);
        String jsonBody = objectMapper.writeValueAsString(registerRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) { // Успешное создание
            return objectMapper.readValue(response.body(), AuthResponse.class);
        } else {
            // Выводим тело ошибки от Spring (там может быть "Email already exists")
            throw new RuntimeException("Registration failed: " + response.body());
        }
    }
}