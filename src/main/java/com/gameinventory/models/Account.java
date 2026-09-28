package com.gameinventory.models;

import java.time.LocalDateTime;

/**
 * Аккаунт игрока. Соответствует строке таблицы account.
 */
public class Account {

    private long accountId;
    private String login;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;
    private boolean banned;

    /** Конструктор для строки, прочитанной из БД (id уже есть). */
    public Account(long accountId, String login, String email, String passwordHash,
                   LocalDateTime createdAt, boolean banned) {
        if (login == null || login.isBlank())
            throw new IllegalArgumentException("Логин обязателен");
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Некорректный email");
        this.accountId = accountId;
        this.login = login.trim();
        this.email = email.trim();
        this.passwordHash = passwordHash;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.banned = banned;
    }

    /** Конструктор для нового аккаунта (id ещё нет). */
    public Account(String login, String email, String passwordHash) {
        this(0, login, email, passwordHash, LocalDateTime.now(), false);
    }

    public long getAccountId() { return accountId; }
    public void setAccountId(long accountId) { this.accountId = accountId; }

    public String getLogin() { return login; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isBanned() { return banned; }
    public void setBanned(boolean banned) { this.banned = banned; }

    @Override
    public String toString() {
        return login + " <" + email + ">";
    }
}