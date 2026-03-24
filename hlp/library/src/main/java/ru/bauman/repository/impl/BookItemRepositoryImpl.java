package ru.bauman.repository.impl;

import ru.bauman.model.BookItem;
import ru.bauman.repository.BookItemRepository;
import ru.bauman.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookItemRepositoryImpl implements BookItemRepository {

    @Override
    public BookItem save(BookItem entity, Connection conn) throws SQLException {
        String sql = "INSERT INTO book_item (book_id, status_id, inventory_number, comment) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, entity.getBookId());
            stmt.setLong(2, entity.getStatusId());
            stmt.setString(3, entity.getInventoryNumber());
            stmt.setString(4, entity.getComment());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
        }
        return entity;
    }

    @Override
    public Optional<BookItem> findById(Long id, Connection conn) throws SQLException {
        String sql = "SELECT id, book_id, status_id, inventory_number, comment FROM book_item WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<BookItem> findAll() throws SQLException {
        List<BookItem> items = new ArrayList<>();
        String sql = "SELECT id, book_id, status_id, inventory_number, comment FROM book_item ORDER BY id";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(mapRow(rs));
            }
        }
        return items;
    }

    @Override
    public void deleteById(Long id, Connection conn) throws SQLException {
        // не требуется
    }

    @Override
    public List<BookItem> findByBookId(Long bookId, Connection conn) throws SQLException {
        List<BookItem> items = new ArrayList<>();
        String sql = "SELECT id, book_id, status_id, inventory_number, comment FROM book_item WHERE book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
            }
        }
        return items;
    }

    @Override
    public Optional<BookItem> findAvailableByBookId(Long bookId, Connection conn) throws SQLException {
        String sql = "SELECT id, book_id, status_id, inventory_number, comment FROM book_item " +
                "WHERE book_id = ? AND status_id = (SELECT id FROM item_status WHERE name = 'AVAILABLE') LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void updateStatus(Long bookItemId, Long statusId, Connection conn) throws SQLException {
        String sql = "UPDATE book_item SET status_id = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, statusId);
            stmt.setLong(2, bookItemId);
            stmt.executeUpdate();
        }
    }

    @Override
    public int countByBookId(Long bookId, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM book_item WHERE book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @Override
    public int countAvailableByBookId(Long bookId, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM book_item WHERE book_id = ? AND status_id = (SELECT id FROM item_status WHERE name = 'AVAILABLE')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private BookItem mapRow(ResultSet rs) throws SQLException {
        return new BookItem(
                rs.getLong("id"),
                rs.getLong("book_id"),
                rs.getLong("status_id"),
                rs.getString("inventory_number"),
                rs.getString("comment")
        );
    }
}