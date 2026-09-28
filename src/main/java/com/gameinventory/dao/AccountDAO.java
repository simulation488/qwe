package com.gameinventory.dao;

import com.gameinventory.models.Account;

import java.sql.*;

/** DAO для таблицы account. */
public class AccountDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    /** Найти аккаунт по id. Возвращает null, если не найден. */
    public Account findById(long id) {
        String sql = "SELECT * FROM account WHERE account_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extract(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка AccountDAO.findById", e);
        }
    }

    /** Найти аккаунт по email. */
    public Account findByEmail(String email) {
        String sql = "SELECT * FROM account WHERE email = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extract(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка AccountDAO.findByEmail", e);
        }
    }

    /** Проверка логина: email + пароль. Возвращает null, если пара неверна. */
    public Account findByEmailAndPassword(String email, String passwordHash) {
        String sql = "SELECT * FROM account WHERE email = ? AND password_hash = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extract(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка AccountDAO.findByEmailAndPassword", e);
        }
    }

    /** Сохранить: если id = 0 — INSERT, иначе UPDATE. */
    public Account save(Account account) {
        if (account.getAccountId() > 0) return update(account);
        return insert(account);
    }

    private Account insert(Account account) {
        String sql = "INSERT INTO account (login, email, password_hash) VALUES (?, ?, ?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getLogin());
            ps.setString(2, account.getEmail());
            ps.setString(3, account.getPasswordHash());
            ps.executeUpdate();
            // Получаем сгенерированный базой account_id
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) account.setAccountId(keys.getLong(1));
            }
            return account;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка AccountDAO.insert", e);
        }
    }

    private Account update(Account account) {
        String sql = "UPDATE account SET login = ?, email = ?, is_banned = ? "
                + "WHERE account_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, account.getLogin());
            ps.setString(2, account.getEmail());
            ps.setBoolean(3, account.isBanned());
            ps.setLong(4, account.getAccountId());
            ps.executeUpdate();
            return account;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка AccountDAO.update", e);
        }
    }

    /** Преобразование строки ResultSet в объект Account. */
    private Account extract(ResultSet rs) throws SQLException {
        return new Account(
                rs.getLong("account_id"),
                rs.getString("login"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBoolean("is_banned"));
    }
}