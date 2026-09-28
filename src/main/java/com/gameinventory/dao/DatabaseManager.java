package com.gameinventory.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton, читает application.properties и выдаёт JDBC-соединения.
 * СУБД выбирается ключом db.type (postgresql | oracle).
 *
 * Это единственное место в проекте, где мы знаем, какая СУБД используется.
 * Все DAO берут отсюда Connection и работают через JDBC.
 */
public class DatabaseManager {

    private static DatabaseManager instance;

    private final String url;
    private final String user;
    private final String password;

    private DatabaseManager() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (in == null) {
                throw new RuntimeException("application.properties не найден в classpath");
            }
            props.load(in);

            String dbType = props.getProperty("db.type", "postgresql").trim().toLowerCase();

            this.url      = props.getProperty("db." + dbType + ".url");
            this.user     = props.getProperty("db." + dbType + ".user");
            this.password = props.getProperty("db." + dbType + ".password");

            if (url == null) {
                throw new RuntimeException(
                        "Не заданы параметры подключения для db.type=" + dbType);
            }

            switch (dbType) {
                case "postgresql" -> Class.forName("org.postgresql.Driver");
                case "oracle"     -> Class.forName("oracle.jdbc.OracleDriver");
                case "mysql"      -> Class.forName("com.mysql.cj.jdbc.Driver");
                default -> throw new RuntimeException("Неизвестный db.type: " + dbType);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Не удалось загрузить настройки БД", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}