package com.gameinventory.dao;

import com.gameinventory.models.CharacterClass;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO для справочника character_class. */
public class CharacterClassDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public List<CharacterClass> findAll() {
        String sql = "SELECT * FROM character_class ORDER BY class_id";
        List<CharacterClass> result = new ArrayList<>();
        try (Connection c = db.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) result.add(extract(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка CharacterClassDAO.findAll", e);
        }
        return result;
    }

    public CharacterClass findById(long id) {
        String sql = "SELECT * FROM character_class WHERE class_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extract(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка CharacterClassDAO.findById", e);
        }
    }

    private CharacterClass extract(ResultSet rs) throws SQLException {
        return new CharacterClass(
                rs.getLong("class_id"),
                rs.getString("class_name"),
                rs.getString("description"));
    }
}