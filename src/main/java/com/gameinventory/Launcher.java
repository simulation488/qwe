package com.gameinventory;

import javafx.application.Application;

/**
 * Точка входа приложения.
 * Отдельный класс — чтобы JVM запускала main из класса, не наследующего Application
 * (иначе JavaFX ругается при запуске из IDE без модульной системы).
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(GameInventoryApp.class, args);
    }
}