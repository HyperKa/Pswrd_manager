package org.example.pswrd_manager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pswrd_manager.entity.User;
import org.example.pswrd_manager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CryptoService cryptoService;
    private final JwtService jwtService;

    // защита от брутфорса
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES = 15;

    @Transactional
    public User register(String username, String masterPassword, String encryptedVault) {
        log.info("AuthService: registering user {}", username);

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("AuthService: username is already in use");
        }

        validatePassword(masterPassword);

        User user = new User();
        user.setUsername(username);
        byte[] salt = cryptoService.generateSalt();
        String authHash = cryptoService.hashPassword(masterPassword, salt);

        user.setSalt(salt);
        user.setAuthHash(authHash);
        user.setEncryptedVault(encryptedVault);
        user.setRole(User.Role.USER);
        user.setFailedAttempts(0);
        user.setLockedUntil(null);

        User savedUser = userRepository.save(user);
        log.info("AuthService: user: {} \tregistered: {}", user, savedUser);
        return savedUser;
    }

    @Transactional
    public User authenticate(String username, String masterPassword) throws IllegalArgumentException {
        log.info("AuthService: authenticating user {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("AuthService: username not found"));

        checkIfLocked(user);

        boolean passwordValid = cryptoService.checkPassword(masterPassword,
                user.getSalt(), user.getAuthHash());

        if (!passwordValid) {
            handleFailedLogin(user);
            throw new IllegalStateException("AuthService: password is invalid");
        }

        user.setLockedUntil(null);
        user.setFailedAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("AuthService: authenticated user: {}", user);

        return user;
    }

    @Transactional
    public byte[] getUserSalt(String username) {
        return userRepository.findByUsername(username)
                .map(User::getSalt)
                .orElse(null);
    }

    @Transactional
    public String getUserEncryptedVault(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("AuthService: username not found"));
        return user.getEncryptedVault();
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword, String newEncryptedVault)
    throws IllegalArgumentException {
        log.info("AuthService: changing password for user {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("AuthService: username not found"));

        if (!cryptoService.checkPassword(oldPassword, user.getSalt(), user.getAuthHash())) {
            throw new IllegalArgumentException("AuthService: password is invalid");
        }

        validatePassword(newPassword);

        byte[] salt = cryptoService.generateSalt();
        String authHash = cryptoService.hashPassword(newPassword, salt);

        user.setSalt(salt);
        user.setAuthHash(authHash);
        user.setEncryptedVault(newEncryptedVault);
        user.setFailedAttempts(0);
        user.setLockedUntil(null);

        User savedUser = userRepository.save(user);
        log.info("AuthService: password changed for user {}", savedUser);
    }

    private void checkIfLocked(User user) {
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "AuthService: Account is locked. Try again after " + user.getLockedUntil()
            );
        }

        if (user.getLockedUntil() != null && user.getLockedUntil().isBefore(LocalDateTime.now())) {
            user.setLockedUntil(null);
            user.setFailedAttempts(0);
        }
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCK_TIME_MINUTES));
            log.warn("AuthService: User {} locked until {} due to too many failed attempts",
                    user.getUsername(), user.getLockedUntil());
        }

        userRepository.save(user);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("AuthService: Password must be at least 8 characters");
        }

        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        if (!hasUpperCase || !hasLowerCase || !hasDigit || !hasSpecial) {
            throw new IllegalArgumentException(
                    "AuthService: Password must contain uppercase, lowercase, digit, and special character (!@#$%^&_*)"
            );
        }
    }

    public void logout(String token) {
        // ЧС будет реализован позже, в другой жизни;)
        log.debug("AuthService: User logged out (in future release)");
    }

}
