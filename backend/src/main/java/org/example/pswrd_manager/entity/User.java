package org.example.pswrd_manager.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "users")
@Data
@Slf4j
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // email
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private byte[] salt;

    // содержит masterPassword
    @Column(nullable = false)
    private String authHash;

    // Хранилище зашифрованных паролей
    @Column(nullable = false)
    private String encryptedVault;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    // дополнение:
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "failed_attempts")
    private Integer failedAttempts;

    public enum Role {
        USER,
        ADMIN
    }
}
