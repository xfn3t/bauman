package ru.bauman.repository.impl;

import ru.bauman.dto.ActiveLoanInfoDto;
import ru.bauman.dto.BookDto;
import ru.bauman.dto.BookWithBorrowDateDto;
import ru.bauman.model.Book;
import ru.bauman.model.Loan;
import ru.bauman.repository.LoanRepository;
import ru.bauman.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoanRepositoryImpl implements LoanRepository {

    @Override
    public Loan save(Loan loan, Connection conn) throws SQLException {
        String sql = "INSERT INTO loans (book_item_id, reader_id, borrow_date, return_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, loan.getBookItemId());
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
        String sql = "SELECT id, book_item_id, reader_id, borrow_date, return_date FROM loans WHERE id = ?";
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
        String sql = "SELECT id, book_item_id, reader_id, borrow_date, return_date FROM loans ORDER BY id";
        try (Connection conn = DatabaseConfig.getConnection();
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
    public Optional<Loan> findActiveLoanByBookItemId(Long bookItemId, Connection conn) throws SQLException {
        String sql = "SELECT id, book_item_id, reader_id, borrow_date, return_date FROM loans " +
                "WHERE book_item_id = ? AND return_date IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, bookItemId);
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
        String sql = "SELECT id, book_item_id, reader_id, borrow_date, return_date FROM loans " +
                "WHERE reader_id = ? AND return_date IS NULL";
        try (Connection conn = DatabaseConfig.getConnection();
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
        String sql = "SELECT id, book_item_id, reader_id, borrow_date, return_date FROM loans WHERE return_date IS NULL";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                loans.add(mapRow(rs));
            }
        }
        return loans;
    }

    @Override
    public List<ActiveLoanInfoDto> findAllActiveLoansWithDetails() throws SQLException {
        String sql = """
        SELECT 
            l.id AS loan_id,
            b.title AS book_title,
            b.author AS book_author,
            r.name AS reader_name,
            l.borrow_date
        FROM loans l
        JOIN book_item bi ON l.book_item_id = bi.id
        JOIN books b ON bi.book_id = b.id
        JOIN readers r ON l.reader_id = r.id
        WHERE l.return_date IS NULL
        ORDER BY l.borrow_date DESC
        """;

        List<ActiveLoanInfoDto> result = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(new ActiveLoanInfoDto(
                        rs.getLong("loan_id"),
                        rs.getString("book_title"),
                        rs.getString("book_author"),
                        rs.getString("reader_name"),
                        rs.getTimestamp("borrow_date")
                ));
            }
        }
        return result;
    }

    @Override
    public List<BookDto> getPopularBooks(int limit) throws SQLException {
        List<BookDto> books = new ArrayList<>();
        String sql = """
            SELECT b.id, b.title, b.author, b.isbn, COUNT(l.id) as borrow_count
            FROM books b
            LEFT JOIN book_item bi ON b.id = bi.book_id
            LEFT JOIN loans l ON bi.id = l.book_item_id
            GROUP BY b.id
            ORDER BY borrow_count DESC
            LIMIT ?
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BookDto dto = new BookDto(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("isbn"),
                            0, 0
                    );
                    books.add(dto);
                }
            }
        }
        return books;
    }

    @Override
    public List<BookWithBorrowDateDto> findBooksIssuedToReaderWithDates(Long readerId) throws SQLException {
        List<BookWithBorrowDateDto> result = new ArrayList<>();
        String sql = """
            SELECT b.id, b.title, b.author, b.isbn, l.borrow_date
            FROM loans l
            JOIN book_item bi ON l.book_item_id = bi.id
            JOIN books b ON bi.book_id = b.id
            WHERE l.reader_id = ? AND l.return_date IS NULL
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, readerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new BookWithBorrowDateDto(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("isbn"),
                            rs.getTimestamp("borrow_date")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<Book> findBooksIssuedToReader(Long readerId) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = """
            SELECT DISTINCT b.id, b.title, b.author, b.isbn
            FROM loans l
            INNER JOIN book_item bi ON l.book_item_id = bi.id
            INNER JOIN books b ON bi.book_id = b.id
            WHERE l.reader_id = ? AND l.return_date IS NULL
            """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, readerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("isbn")
                    );
                    books.add(book);
                }
            }
        }
        return books;
    }

    private Loan mapRow(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getLong("id"));
        loan.setBookItemId(rs.getLong("book_item_id"));
        loan.setReaderId(rs.getLong("reader_id"));
        loan.setBorrowDate(rs.getTimestamp("borrow_date"));
        loan.setReturnDate(rs.getTimestamp("return_date"));
        return loan;
    }
}