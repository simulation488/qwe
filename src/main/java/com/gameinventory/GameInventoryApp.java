package com.gameinventory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Главный класс JavaFX-приложения.
 * Загружает стартовый экран (main-view.fxml).
 */
public class GameInventoryApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                GameInventoryApp.class.getResource("main-view.fxml"));
        Scene scene = new Scene(loader.load(), 1100, 700);
        stage.setTitle("Учёт игрового инвентаря");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}