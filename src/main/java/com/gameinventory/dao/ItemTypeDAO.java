package com.gameinventory.dao;

import com.gameinventory.models.ItemType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO для справочника item_type. */
public class ItemTypeDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public List<ItemType> findAll() {
        String sql = "SELECT * FROM item_type ORDER BY item_type_id";
        List<ItemType> result = new ArrayList<>();
        try (Connection c = db.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new ItemType(rs.getLong("item_type_id"), rs.getString("type_name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка ItemTypeDAO.findAll", e);
        }
        return result;
    }

    public ItemType findById(long id) {
        String sql = "SELECT * FROM item_type WHERE item_type_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new ItemType(rs.getLong("item_type_id"), rs.getString("type_name"));
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка ItemTypeDAO.findById", e);
        }
    }
}