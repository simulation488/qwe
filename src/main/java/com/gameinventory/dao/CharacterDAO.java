package com.gameinventory.dao;

import com.gameinventory.models.Account;
import com.gameinventory.models.CharacterClass;
import com.gameinventory.models.GameCharacter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO для таблицы game_character. */
public class CharacterDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private final AccountDAO accountDAO = new AccountDAO();
    private final CharacterClassDAO classDAO = new CharacterClassDAO();

    /** Все персонажи одного аккаунта. */
    public List<GameCharacter> findByAccountId(long accountId) {
        String sql = "SELECT * FROM game_character WHERE account_id = ? ORDER BY character_id";
        List<GameCharacter> result = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(extract(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка CharacterDAO.findByAccountId", e);
        }
        return result;
    }

    public GameCharacter findById(long id) {
        String sql = "SELECT * FROM game_character WHERE character_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extract(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка CharacterDAO.findById", e);
        }
    }

    public GameCharacter save(GameCharacter ch) {
        if (ch.getCharacterId() > 0) return update(ch);
        return insert(ch);
    }

    private GameCharacter insert(GameCharacter ch) {
        String sql = "INSERT INTO game_character (account_id, name, class_id, level, experience) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, ch.getAccount().getAccountId());
            ps.setString(2, ch.getName());
            ps.setLong(3, ch.getCharacterClass().getClassId());
            ps.setInt(4, ch.getLevel());
            ps.setLong(5, ch.getExperience());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) ch.setCharacterId(keys.getLong(1));
            }
            return ch;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка CharacterDAO.insert", e);
        }
    }

    private GameCharacter update(GameCharacter ch) {
        String sql = "UPDATE game_character SET name = ?, class_id = ?, level = ?, experience = ? "
                + "WHERE character_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ch.getName());
            ps.setLong(2, ch.getCharacterClass().getClassId());
            ps.setInt(3, ch.getLevel());
            ps.setLong(4, ch.getExperience());
            ps.setLong(5, ch.getCharacterId());
            ps.executeUpdate();
            return ch;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка CharacterDAO.update", e);
        }
    }

    private GameCharacter extract(ResultSet rs) throws SQLException {
        // Подгружаем связанные сущности — это заменяет JOIN на две отдельные выборки.
        Account acc = accountDAO.findById(rs.getLong("account_id"));
        CharacterClass cc = classDAO.findById(rs.getLong("class_id"));
        return new GameCharacter(
                rs.getLong("character_id"),
                acc,
                rs.getString("name"),
                cc,
                rs.getInt("level"),
                rs.getLong("experience"),
                rs.getTimestamp("created_at").toLocalDateTime());
    }
}