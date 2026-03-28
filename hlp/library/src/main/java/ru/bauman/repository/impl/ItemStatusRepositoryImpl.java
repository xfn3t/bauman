package ru.bauman.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import ru.bauman.model.ItemStatus;
import ru.bauman.repository.ItemStatusRepository;
import ru.bauman.config.DatabaseConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemStatusRepositoryImpl implements ItemStatusRepository {

    @Override
    public ItemStatus save(ItemStatus entity, Connection conn) throws SQLException {
        // не используется, статусы предзаполнены
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ItemStatus> findById(Long id, Connection conn) throws SQLException {
        String sql = "SELECT id, name FROM item_status WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ItemStatus(rs.getLong("id"), rs.getString("name")));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ItemStatus> findAll() throws SQLException {
        List<ItemStatus> statuses = new ArrayList<>();
        String sql = "SELECT id, name FROM item_status ORDER BY id";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                statuses.add(new ItemStatus(rs.getLong("id"), rs.getString("name")));
            }
        }
        return statuses;
    }

    @Override
    public void deleteById(Long id, Connection conn) throws SQLException {
        // не требуется
    }

    @Override
    public Optional<Long> getStatusIdByName(String name, Connection conn) throws SQLException {
        String sql = "SELECT id FROM item_status WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong("id"));
                }
            }
        }
        return Optional.empty();
    }
}