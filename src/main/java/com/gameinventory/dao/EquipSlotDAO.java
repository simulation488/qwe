package com.gameinventory.dao;

import com.gameinventory.models.EquipSlot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO для справочника equip_slot. */
public class EquipSlotDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public List<EquipSlot> findAll() {
        String sql = "SELECT * FROM equip_slot ORDER BY slot_id";
        List<EquipSlot> result = new ArrayList<>();
        try (Connection c = db.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new EquipSlot(rs.getLong("slot_id"), rs.getString("slot_name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка EquipSlotDAO.findAll", e);
        }
        return result;
    }

    public EquipSlot findById(long id) {
        String sql = "SELECT * FROM equip_slot WHERE slot_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new EquipSlot(rs.getLong("slot_id"), rs.getString("slot_name"));
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка EquipSlotDAO.findById", e);
        }
    }
}