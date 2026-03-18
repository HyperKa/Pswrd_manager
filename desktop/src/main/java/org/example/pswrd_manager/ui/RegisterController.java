package org.example.pswrd_manager.ui;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import org.example.pswrd_manager.App;
import org.example.pswrd_manager.service.AuthService;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;

public class RegisterController {
    // Проверьте, что эти имена СОВПАДАЮТ с fx:id в FXML
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField; // Добавьте это поле!
    @FXML private Label statusLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void onRegisterClick() {
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (!pass.equals(confirm)) {
            statusLabel.setText("Пароли не совпадают!");
            return;
        }

        try {
            authService.register(usernameField.getText(), pass, "{}");
            statusLabel.setText("Успех! Возвращайтесь к логину.");
        } catch (Exception e) {
            statusLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void onBackToLogin() throws IOException {
        App.setRoot("login-view"); // Теперь этот метод будет работать
    }
}
