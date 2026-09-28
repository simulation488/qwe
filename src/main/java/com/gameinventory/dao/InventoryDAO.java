package com.gameinventory.dao;

import com.gameinventory.models.GameCharacter;
import com.gameinventory.models.Inventory;

import java.sql.*;

/** DAO для таблицы inventory. */
public class InventoryDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private final CharacterDAO characterDAO = new CharacterDAO();

    /** Найти инвентарь персонажа. */
    public Inventory findByCharacterId(long characterId) {
        String sql = "SELECT * FROM inventory WHERE character_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extract(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка InventoryDAO.findByCharacterId", e);
        }
    }

    /** Найти инвентарь по его id. */
    public Inventory findById(long inventoryId) {
        String sql = "SELECT * FROM inventory WHERE inventory_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, inventoryId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extract(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка InventoryDAO.findById", e);
        }
    }

    /** Создать новый инвентарь. */
    public Inventory create(Inventory inv) {
        String sql = "INSERT INTO inventory (character_id, max_slots) VALUES (?, ?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, inv.getCharacter().getCharacterId());
            ps.setInt(2, inv.getMaxSlots());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) inv.setInventoryId(keys.getLong(1));
            }
            return inv;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка InventoryDAO.create", e);
        }
    }

    private Inventory extract(ResultSet rs) throws SQLException {
        GameCharacter ch = characterDAO.findById(rs.getLong("character_id"));
        return new Inventory(
                rs.getLong("inventory_id"),
                ch,
                rs.getInt("max_slots"));
    }
}