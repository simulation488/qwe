package com.gameinventory.dao;

import com.gameinventory.models.Inventory;
import com.gameinventory.models.InventorySlot;
import com.gameinventory.models.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO для таблицы inventory_slot. */
public class InventorySlotDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final ItemDAO itemDAO = new ItemDAO();

    /** Все слоты одного инвентаря, упорядоченные по номеру. */
    public List<InventorySlot> findByInventoryId(long inventoryId) {
        String sql = "SELECT * FROM inventory_slot WHERE inventory_id = ? ORDER BY slot_number";
        List<InventorySlot> result = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, inventoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(extract(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка InventorySlotDAO.findByInventoryId", e);
        }
        return result;
    }

    public InventorySlot save(InventorySlot slot) {
        if (slot.getSlotId() > 0) return update(slot);
        return insert(slot);
    }

    private InventorySlot insert(InventorySlot slot) {
        String sql = "INSERT INTO inventory_slot (inventory_id, slot_number, item_id, quantity, "
                + "is_equipped) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, slot.getInventory().getInventoryId());
            ps.setInt(2, slot.getSlotNumber());
            if (slot.getItem() != null)
                ps.setLong(3, slot.getItem().getItemId());
            else
                ps.setNull(3, Types.BIGINT);
            ps.setInt(4, slot.getQuantity());
            ps.setBoolean(5, slot.isEquipped());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) slot.setSlotId(keys.getLong(1));
            }
            return slot;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка InventorySlotDAO.insert", e);
        }
    }

    private InventorySlot update(InventorySlot slot) {
        String sql = "UPDATE inventory_slot SET slot_number = ?, item_id = ?, quantity = ?, "
                + "is_equipped = ? WHERE slot_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, slot.getSlotNumber());
            if (slot.getItem() != null)
                ps.setLong(2, slot.getItem().getItemId());
            else
                ps.setNull(2, Types.BIGINT);
            ps.setInt(3, slot.getQuantity());
            ps.setBoolean(4, slot.isEquipped());
            ps.setLong(5, slot.getSlotId());
            ps.executeUpdate();
            return slot;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка InventorySlotDAO.update", e);
        }
    }

    public boolean delete(long slotId) {
        String sql = "DELETE FROM inventory_slot WHERE slot_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, slotId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка InventorySlotDAO.delete", e);
        }
    }

    private InventorySlot extract(ResultSet rs) throws SQLException {
        // Inventory по inventory_id
        Inventory inv = inventoryDAO.findById(rs.getLong("inventory_id"));
        long itemId = rs.getLong("item_id");
        Item item = rs.wasNull() ? null : itemDAO.findById(itemId);
        return new InventorySlot(
                rs.getLong("slot_id"),
                inv,
                rs.getInt("slot_number"),
                item,
                rs.getInt("quantity"),
                rs.getBoolean("is_equipped"));
    }
}