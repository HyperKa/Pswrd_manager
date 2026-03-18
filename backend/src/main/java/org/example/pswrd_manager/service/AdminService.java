package org.example.pswrd_manager.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pswrd_manager.entity.User;
import org.example.pswrd_manager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final CryptoService cryptoService;

    @Transactional
    public User register(String username, String password, String encryptedVault) throws IllegalArgumentException {
        // проверка на существование:
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setRole(User.Role.ADMIN);

        byte[] salt = cryptoService.generateSalt();
        String hash = cryptoService.hashPassword(password, salt);

        user.setSalt(salt);
        user.setAuthHash(hash);
        user.setEncryptedVault(encryptedVault);

        return userRepository.save(user);

    }

    @Transactional
    public void deleteUser(String username) throws IllegalArgumentException {
        // Обработка на удаление единственного админа и существование удаляемого пользователя
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("AdminService: User not found"));
        if (user.getRole() == User.Role.ADMIN) {
            long adminCount = userRepository.countByRole(User.Role.ADMIN);
            if (adminCount <= 1) {
                throw new IllegalStateException("AdminService: cannot delete the last admin");
            }
        }

        log.info("AdminService: delete user {}", username);

        userRepository.delete(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
