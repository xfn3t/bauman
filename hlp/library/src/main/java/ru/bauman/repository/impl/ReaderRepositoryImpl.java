package ru.bauman.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.stream.Collectors;
import ru.bauman.dto.BookDto;
import ru.bauman.dto.ReaderDto;
import ru.bauman.dto.ReaderWithBooksDto;
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

    @Override
    public List<ReaderWithBooksDto> findAllReadersWithActiveBooks() throws SQLException {
        String sql = """
            SELECT 
                r.id AS reader_id, r.name AS reader_name, r.email, r.phone,
                b.id AS book_id, b.title, b.author, b.isbn
            FROM readers r
            LEFT JOIN loans l ON r.id = l.reader_id AND l.return_date IS NULL
            LEFT JOIN book_item bi ON l.book_item_id = bi.id
            LEFT JOIN books b ON bi.book_id = b.id
            ORDER BY r.id, b.id
        """;

        Map<Long, ReaderWithBooksDto.Builder> readerMap = new LinkedHashMap<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Long readerId = rs.getLong("reader_id");
                String readerName = rs.getString("reader_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");

                ReaderDto readerDto = new ReaderDto(readerId, readerName, email, phone);

                ReaderWithBooksDto.Builder builder = readerMap.computeIfAbsent(readerId,
                        id -> new ReaderWithBooksDto.Builder(readerDto));

                long bookId = rs.getLong("book_id");
                if (!rs.wasNull()) {
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    String isbn = rs.getString("isbn");
                    BookDto book = new BookDto(bookId, title, author, isbn, 0, 0);
                    builder.addBook(book);
                }
            }
        }

        return readerMap.values().stream()
                .map(ReaderWithBooksDto.Builder::build)
                .collect(Collectors.toList());
    }
}