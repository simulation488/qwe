package com.gameinventory.controllers;

import com.gameinventory.models.Account;
import com.gameinventory.models.GameCharacter;
import com.gameinventory.models.GameManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Контроллер главного экрана.
 * Логин, список персонажей, переход в инвентарь.
 */
public class MainController {

    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label         statusLabel;
    @FXML private Label         charactersHeader;
    @FXML private VBox          charactersContainer;

    private final GameManager manager = GameManager.getInstance();

    @FXML
    public void initialize() {
        charactersContainer.setVisible(false);
        charactersHeader.setVisible(false);
    }

    @FXML
    private void onLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Заполните email и пароль");
            return;
        }

        Account account = manager.login(email, password);
        if (account == null) {
            statusLabel.setText("Неверный email или пароль");
            return;
        }

        statusLabel.setText("Вы вошли как " + account.getEmail());
        showCharacters();
    }

    private void showCharacters() {
        charactersContainer.getChildren().clear();
        List<GameCharacter> characters = manager.getCharactersOfCurrentAccount();

        if (characters.isEmpty()) {
            charactersContainer.getChildren().add(new Label("У аккаунта нет персонажей"));
        } else {
            for (GameCharacter ch : characters) {
                Button btn = new Button(ch.toString());
                btn.setPrefWidth(400);
                btn.setOnAction(e -> openCharacter(ch));
                charactersContainer.getChildren().add(btn);
            }
        }
        charactersContainer.setVisible(true);
        charactersHeader.setVisible(true);
    }

    private void openCharacter(GameCharacter ch) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/com/gameinventory/character-view.fxml"));
            Parent root = loader.load();

            CharacterController controller = loader.getController();
            controller.setCharacter(ch);

            Stage stage = (Stage) charactersContainer.getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 700));
            stage.setTitle("Инвентарь: " + ch.getName());
        } catch (IOException e) {
            statusLabel.setText("Ошибка открытия инвентаря: " + e.getMessage());
        }
    }
}