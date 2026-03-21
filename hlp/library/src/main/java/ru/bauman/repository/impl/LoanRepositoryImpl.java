package ru.bauman.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ru.bauman.model.Loan;
import ru.bauman.model.Book;
import ru.bauman.repository.LoanRepository;

public class LoanRepositoryImpl implements LoanRepository {

    @Override
    public Loan save(Loan loan, Connection conn) throws SQLException {
        String sql = "INSERT INTO loans (book_id, reader_id, borrow_date, return_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, loan.getBookId());
            stmt.setLong(2, loan.getReaderId());
            stmt.setTimestamp(3, loan.getBorrowDate());
            stmt.setTimestamp(4, loan.getReturnDate());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    loan.setId(rs.getLong(1));
                }
            }
        }
        return loan;
    }

    @Override
    public Optional<Loan> findById(Long id, Connection conn) throws SQLException {
        String sql = "SELECT id, book_id, reader_id, borrow_date, return_date FROM loans WHERE id = ?";
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
    public List<Loan> findAll() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT id, book_id, reader_id, borrow_date, return_date FROM loans ORDER BY id";
        try (Connection conn = ru.bauman.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                loans.add(mapRow(rs));
            }
        }
        return loans;
    }

    @Override
    public void deleteById(Long id, Connection conn) throws SQLException {
        // не требуется
    }

    @Override
    public Optional<Loan> findActiveLoanByBookId(Long bookId, Connection conn) throws SQLException {
        String sql = "SELECT id, book_id, reader_id, borrow_date, return_date FROM loans " +
                "WHERE book_id = ? AND return_date IS NULL";
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
    public List<Loan> findActiveLoansByReaderId(Long readerId) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT id, book_id, reader_id, borrow_date, return_date FROM loans " +
                "WHERE reader_id = ? AND return_date IS NULL";
        try (Connection conn = ru.bauman.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, readerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapRow(rs));
                }
            }
        }
        return loans;
    }

    @Override
    public void returnBook(Long loanId, Connection conn) throws SQLException {
        String sql = "UPDATE loans SET return_date = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, loanId);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Loan> findAllActiveLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT id, book_id, reader_id, borrow_date, return_date FROM loans WHERE return_date IS NULL";
        try (Connection conn = ru.bauman.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                loans.add(mapRow(rs));
            }
        }
        return loans;
    }

    @Override
    public List<ru.bauman.dto.BookDto> getPopularBooks(int limit) throws SQLException {
        List<ru.bauman.dto.BookDto> books = new ArrayList<>();
        String sql = """
            SELECT b.id, b.title, b.author, b.isbn, b.available, COUNT(l.id) as borrow_count
            FROM books b
            LEFT JOIN loans l ON b.id = l.book_id
            GROUP BY b.id
            ORDER BY borrow_count DESC
            LIMIT ?
        """;
        try (Connection conn = ru.bauman.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ru.bauman.dto.BookDto dto = new ru.bauman.dto.BookDto(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("isbn"),
                            rs.getBoolean("available")
                    );
                    books.add(dto);
                }
            }
        }
        return books;
    }

    @Override
    public List<Book> findBooksIssuedToReader(Long readerId) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = """
            SELECT b.id, b.title, b.author, b.isbn, b.available
            FROM loans l
            INNER JOIN books b ON l.book_id = b.id
            WHERE l.reader_id = ? AND l.return_date IS NULL
        """;
        try (Connection conn = ru.bauman.config.DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, readerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("isbn"),
                            rs.getBoolean("available")
                    );
                    books.add(book);
                }
            }
        }
        return books;
    }

    private ru.bauman.model.Loan mapRow(ResultSet rs) throws SQLException {
        ru.bauman.model.Loan loan = new ru.bauman.model.Loan();
        loan.setId(rs.getLong("id"));
        loan.setBookId(rs.getLong("book_id"));
        loan.setReaderId(rs.getLong("reader_id"));
        loan.setBorrowDate(rs.getTimestamp("borrow_date"));
        loan.setReturnDate(rs.getTimestamp("return_date"));
        return loan;
    }
}