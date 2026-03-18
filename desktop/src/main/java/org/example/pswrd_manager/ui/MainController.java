package org.example.pswrd_manager.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.example.pswrd_manager.dto.AuthResponse;
import org.example.pswrd_manager.dto.VaultEntry;
import org.example.pswrd_manager.service.VaultService;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.UUID;

public class MainController {
    @FXML private TableView<VaultEntry> passwordTable;
    @FXML private TableColumn<VaultEntry, String> siteColumn;
    @FXML private TableColumn<VaultEntry, String> userColumn;
    @FXML private TableColumn<VaultEntry, String> passwordColumn;
    @FXML private HBox adminPanel; // Нужно добавить в FXML fx:id="adminPanel"
    private final VaultService vaultService  = new VaultService();;
    private SecretKey vaultKey;

    private final ObservableList<VaultEntry> vaultEntries = FXCollections.observableArrayList();
    private AuthResponse session;

    // Этот метод вызывается вручную из LoginController
    public void initData(AuthResponse authData, SecretKey key) {
        this.session = authData;
        this.vaultKey = key;

        // Скрываем админку если нужно
        if (adminPanel != null) {
            adminPanel.setVisible("ADMIN".equals(authData.getRole().name()));
        }

        // ПЕРВЫЙ ЗАПУСК: Загружаем пароли из БД сразу при входе
        refreshVault();
    }

    @FXML
    public void initialize() {
        // Базовая настройка таблицы
        siteColumn.setCellValueFactory(new PropertyValueFactory<>("siteName"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        passwordTable.setItems(vaultEntries);
    }

    @FXML
    private void addNewPassword() {
        Dialog<VaultEntry> dialog = new Dialog<>();
        dialog.setTitle("Добавить новый пароль");
        dialog.setHeaderText("Введите данные аккаунта");

        // Кнопки
        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Поля ввода
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField siteField = new TextField();
        siteField.setPromptText("Сайт (напр. Google)");
        TextField loginField = new TextField();
        loginField.setPromptText("Логин/Email");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Пароль");

        grid.add(new Label("Сайт:"), 0, 0);
        grid.add(siteField, 1, 0);
        grid.add(new Label("Логин:"), 0, 1);
        grid.add(loginField, 1, 1);
        grid.add(new Label("Пароль:"), 0, 2);
        grid.add(passField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Результат диалога
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new VaultEntry(
                        UUID.randomUUID().toString(),
                        siteField.getText(),
                        loginField.getText(),
                        passField.getText(),
                        ""
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newEntry -> {
            vaultEntries.add(newEntry);
            saveVaultToServer(); // Отправляем обновленный список на бэкенд
        });
    }

    @FXML
    private void refreshVault() {
        try {
            // 1. Загружаем и расшифровываем список
            List<VaultEntry> remoteEntries = vaultService.loadVault(session.getToken(), vaultKey);

            // 2. Обновляем таблицу
            vaultEntries.setAll(remoteEntries);
            System.out.println("Загружено " + remoteEntries.size() + " паролей");
        } catch (Exception e) {
            System.err.println("Ошибка загрузки: " + e.getMessage());
            // Если в базе было "{}", loadVault может выкинуть ошибку или вернуть пустой список
        }
    }

    private void saveVaultToServer() {
        try {
            // 1. Превращаем ObservableList в обычный ArrayList
            List<VaultEntry> listToSave = new java.util.ArrayList<>(vaultEntries);

            // 2. Вызываем сервис, передавая:
            // - список данных
            // - JWT токен из сессии
            // - ключ шифрования
            vaultService.save(listToSave, session.getToken(), vaultKey);

            System.out.println("Сейф успешно синхронизирован с сервером!");

        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
            // В идеале показать ошибку пользователю через Alert
            Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось сохранить данные: " + e.getMessage());
            alert.showAndWait();
        }
    }
}