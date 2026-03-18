package org.example.pswrd_manager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pswrd_manager.dto.ApiResponse;
import org.example.pswrd_manager.dto.RegisterRequest;
import org.example.pswrd_manager.dto.UserProfileResponse;
import org.example.pswrd_manager.entity.User;
import org.example.pswrd_manager.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers(Principal principal) {

        String adminUsername = principal.getName();
        log.info("Admin {} requested all users", adminUsername);

        List<User> users = adminService.findAllUsers();
        List<UserProfileResponse> response = users.stream()
                .map(UserProfileResponse::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users")
    public ResponseEntity<UserProfileResponse> createAdmin(
            @Valid @RequestBody RegisterRequest request,
            Principal principal) {

        User newAdmin = adminService.register(
                request.getUsername(),
                request.getMasterPassword(),
                request.getEncryptedVault()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserProfileResponse(newAdmin));
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<ApiResponse> deleteUser(
            @PathVariable String username,
            Principal principal) {

        if (principal.getName().equals(username)) {
            throw new IllegalArgumentException("Admin cannot delete themselves");
        }

        adminService.deleteUser(username);

        return ResponseEntity.ok(new ApiResponse("User " + username + " deleted successfully"));
    }
}
