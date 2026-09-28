package com.gameinventory.dao;

import com.gameinventory.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO для таблицы item. */
public class ItemDAO {

    private final DatabaseManager db = DatabaseManager.getInstance();
    private final ItemTypeDAO typeDAO = new ItemTypeDAO();
    private final QualityDAO qualityDAO = new QualityDAO();
    private final EquipSlotDAO equipSlotDAO = new EquipSlotDAO();

    public List<Item> findAll() {
        String sql = "SELECT * FROM item ORDER BY item_id";
        List<Item> result = new ArrayList<>();
        try (Connection c = db.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) result.add(extract(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка ItemDAO.findAll", e);
        }
        return result;
    }

    public Item findById(long id) {
        String sql = "SELECT * FROM item WHERE item_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extract(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка ItemDAO.findById", e);
        }
    }

    public Item save(Item item) {
        if (item.getItemId() > 0) return update(item);
        return insert(item);
    }

    private Item insert(Item item) {
        String sql = "INSERT INTO item (item_name, item_type_id, quality_id, required_level, "
                + "max_stack, is_equippable, equip_slot_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getItemName());
            ps.setLong(2, item.getItemType().getItemTypeId());
            ps.setLong(3, item.getQuality().getQualityId());
            ps.setInt(4, item.getRequiredLevel());
            ps.setInt(5, item.getMaxStack());
            ps.setBoolean(6, item.isEquippable());
            // equip_slot_id может быть NULL — обрабатываем отдельно
            if (item.getEquipSlot() != null)
                ps.setLong(7, item.getEquipSlot().getSlotId());
            else
                ps.setNull(7, Types.BIGINT);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) item.setItemId(keys.getLong(1));
            }
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка ItemDAO.insert", e);
        }
    }

    private Item update(Item item) {
        String sql = "UPDATE item SET item_name = ?, item_type_id = ?, quality_id = ?, "
                + "required_level = ?, max_stack = ?, is_equippable = ?, equip_slot_id = ? "
                + "WHERE item_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, item.getItemName());
            ps.setLong(2, item.getItemType().getItemTypeId());
            ps.setLong(3, item.getQuality().getQualityId());
            ps.setInt(4, item.getRequiredLevel());
            ps.setInt(5, item.getMaxStack());
            ps.setBoolean(6, item.isEquippable());
            if (item.getEquipSlot() != null)
                ps.setLong(7, item.getEquipSlot().getSlotId());
            else
                ps.setNull(7, Types.BIGINT);
            ps.setLong(8, item.getItemId());
            ps.executeUpdate();
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка ItemDAO.update", e);
        }
    }

    private Item extract(ResultSet rs) throws SQLException {
        ItemType type = typeDAO.findById(rs.getLong("item_type_id"));
        Quality quality = qualityDAO.findById(rs.getLong("quality_id"));
        // equip_slot_id может быть NULL
        long slotId = rs.getLong("equip_slot_id");
        EquipSlot slot = rs.wasNull() ? null : equipSlotDAO.findById(slotId);

        return new Item(
                rs.getLong("item_id"),
                rs.getString("item_name"),
                type,
                quality,
                rs.getInt("required_level"),
                rs.getInt("max_stack"),
                rs.getBoolean("is_equippable"),
                slot);
    }
}