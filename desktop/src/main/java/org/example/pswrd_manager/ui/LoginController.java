package org.example.pswrd_manager.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.pswrd_manager.App;
import org.example.pswrd_manager.dto.AuthResponse;
import org.example.pswrd_manager.service.AuthService;
import org.example.pswrd_manager.service.CryptoService;

import javax.crypto.SecretKey;
import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private Button loginButton;

    private final AuthService authService = new AuthService();

    @FXML
    private void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            // Реальный запрос к бэкенду
            AuthResponse response = authService.login(username, password);

            // Если успех, переключаем экран и передаем данные
            byte[] salt = "fixed_vault_salt".getBytes();
            SecretKey vaultKey = new CryptoService().deriveKey(password, salt);
            switchToMain(response, vaultKey);

        } catch (Exception e) {
            statusLabel.setText("Ошибка: " + e.getMessage());
        }
    }

    private void switchToMain(AuthResponse authData, SecretKey vaultKey) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Scene mainScene = new Scene(loader.load());

        MainController mainController = loader.getController();
        // Передаем и данные сессии, и секретный ключ!
        mainController.initData(authData, vaultKey);

        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(mainScene);
    }

    @FXML
    private void onOpenRegister() throws IOException {
        App.setRoot("register-view");
    }
}