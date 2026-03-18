package org.example.pswrd_manager.controller;

import org.example.pswrd_manager.dto.*;
import org.example.pswrd_manager.entity.User;
import org.example.pswrd_manager.repository.UserRepository;
import org.example.pswrd_manager.service.AuthService;
import org.example.pswrd_manager.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(
                request.getUsername(),
                request.getMasterPassword(),
                request.getEncryptedVault()
        );

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(
                        token,
                        user.getUsername(),
                        user.getRole(),
                        jwtService.getExpirationTime()
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.authenticate(
                request.getUsername(),
                request.getMasterPassword()
        );

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        return ResponseEntity.ok(new AuthResponse(
                token,
                user.getUsername(),
                user.getRole(),
                jwtService.getExpirationTime()
        ));
    }

    @GetMapping("/vault")
    public ResponseEntity<VaultDto> getVault(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        // JSON вида {"encryptedVault": "..."}, и так в JSON идут все поля из User
        return ResponseEntity.ok(new  VaultDto(user.getEncryptedVault()));
    }

    @PostMapping("/vault")
    public ResponseEntity<ApiResponse> updateVault(@RequestBody VaultDto vaultDto, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("username not found"));

        user.setEncryptedVault(vaultDto.getEncryptedVault());
        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse("Vault updated successfully"));
    }

    @GetMapping("/salt/{username}")
    public ResponseEntity<SaltResponse> getSalt(@PathVariable String username) {
        byte[] salt = authService.getUserSalt(username);
        return ResponseEntity.ok(new SaltResponse(salt));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String token) {
        // Пока искусственно
        return ResponseEntity.ok(new ApiResponse("Successfully logged out"));
    }
}
