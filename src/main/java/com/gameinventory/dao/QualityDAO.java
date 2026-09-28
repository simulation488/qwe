package com.gameinventory.dao;

import com.gameinventory.models.Quality;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO для справочника quality. */
public class QualityDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();

    public List<Quality> findAll() {
        String sql = "SELECT * FROM quality ORDER BY quality_id";
        List<Quality> result = new ArrayList<>();
        try (Connection c = db.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) result.add(extract(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка QualityDAO.findAll", e);
        }
        return result;
    }

    public Quality findById(long id) {
        String sql = "SELECT * FROM quality WHERE quality_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extract(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка QualityDAO.findById", e);
        }
    }

    private Quality extract(ResultSet rs) throws SQLException {
        return new Quality(
                rs.getLong("quality_id"),
                rs.getString("quality_name"),
                rs.getString("color_code"));
    }
}