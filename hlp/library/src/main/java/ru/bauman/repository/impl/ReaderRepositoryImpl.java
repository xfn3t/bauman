package ru.bauman.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ru.bauman.model.Reader;
import ru.bauman.config.DatabaseConfig;
import ru.bauman.repository.ReaderRepository;

public class ReaderRepositoryImpl implements ReaderRepository {

    @Override
    public Reader save(Reader reader, Connection conn) throws SQLException {
        String sql = "INSERT INTO readers (name, email, phone) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, reader.getName());
            stmt.setString(2, reader.getEmail());
            stmt.setString(3, reader.getPhone());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    reader.setId(rs.getLong(1));
                }
            }
        }
        return reader;
    }

    @Override
    public Optional<Reader> findById(Long id, Connection conn) throws SQLException {
        String sql = "SELECT id, name, email, phone FROM readers WHERE id = ?";
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
    public List<Reader> findAll() throws SQLException {
        List<Reader> readers = new ArrayList<>();
        String sql = "SELECT id, name, email, phone FROM readers ORDER BY id";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                readers.add(mapRow(rs));
            }
        }
        return readers;
    }

    @Override
    public void deleteById(Long id, Connection conn) throws SQLException {
        // не требуется
    }

    private Reader mapRow(ResultSet rs) throws SQLException {
        Reader reader = new Reader();
        reader.setId(rs.getLong("id"));
        reader.setName(rs.getString("name"));
        reader.setEmail(rs.getString("email"));
        reader.setPhone(rs.getString("phone"));
        return reader;
    }
}