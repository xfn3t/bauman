package ru.bauman.serive.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ru.bauman.common.exception.BookNotAvailableException;
import ru.bauman.common.exception.BookNotFoundException;
import ru.bauman.common.exception.ReaderNotFoundException;
import ru.bauman.config.DatabaseConfig;
import ru.bauman.dto.ActiveLoanInfoDto;
import ru.bauman.dto.BookDto;
import ru.bauman.dto.BookWithBorrowDateDto;
import ru.bauman.dto.LoanDto;
import ru.bauman.mapper.BookMapper;
import ru.bauman.mapper.LoanMapper;
import ru.bauman.model.Book;
import ru.bauman.model.BookItem;
import ru.bauman.model.Loan;
import ru.bauman.model.Reader;
import ru.bauman.repository.BookRepository;
import ru.bauman.repository.BookItemRepository;
import ru.bauman.repository.LoanRepository;
import ru.bauman.repository.ReaderRepository;
import ru.bauman.serive.LoanService;

@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;
    private final ReaderRepository readerRepository;
    private final LoanRepository loanRepository;

    private final int DEFAULT_TOP_SIZE = 10;

    @Override
    public void issueBook(Long bookId, Long readerId) throws SQLException, BookNotFoundException, ReaderNotFoundException, BookNotAvailableException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                bookRepository.findById(bookId, conn)
                        .orElseThrow(() -> new BookNotFoundException("Книга с ID " + bookId + " не найдена"));

                readerRepository.findById(readerId, conn)
                        .orElseThrow(() -> new ReaderNotFoundException("Читатель с ID " + readerId + " не найден"));

                Optional<BookItem> availableItem = bookItemRepository.findAvailableByBookId(bookId, conn);
                if (availableItem.isEmpty()) {
                    throw new BookNotAvailableException("Нет доступных экземпляров книги");
                }

                BookItem item = availableItem.get();

                Loan loan = new Loan();
                loan.setBookItemId(item.getId());
                loan.setReaderId(readerId);
                loan.setBorrowDate(new Timestamp(System.currentTimeMillis()));
                loanRepository.save(loan, conn);

                Long loanedStatusId = getStatusIdByName("LOANED", conn);
                bookItemRepository.updateStatus(item.getId(), loanedStatusId, conn);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public void returnBook(Long bookId) throws SQLException, BookNotFoundException, BookNotAvailableException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Book book = bookRepository.findById(bookId, conn)
                        .orElseThrow(() -> new BookNotFoundException("Книга с ID " + bookId + " не найдена"));

                List<BookItem> items = bookItemRepository.findByBookId(bookId, conn);
                BookItem loanedItem = null;
                Long loanedStatusId = getStatusIdByName("LOANED", conn);
                for (BookItem item : items) {
                    if (item.getStatusId().equals(loanedStatusId)) {
                        loanedItem = item;
                        break;
                    }
                }
                if (loanedItem == null) {
                    throw new BookNotAvailableException("Нет выданных экземпляров этой книги");
                }

                Optional<Loan> activeLoan = loanRepository.findActiveLoanByBookItemId(loanedItem.getId(), conn);
                if (activeLoan.isEmpty()) {
                    throw new BookNotAvailableException("Нет активной выдачи для этого экземпляра");
                }

                loanRepository.returnBook(activeLoan.get().getId(), conn);

                Long availableStatusId = getStatusIdByName("AVAILABLE", conn);
                bookItemRepository.updateStatus(loanedItem.getId(), availableStatusId, conn);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<BookDto> getBooksIssuedToReader(Long readerId) throws SQLException, ReaderNotFoundException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            readerRepository.findById(readerId, conn)
                    .orElseThrow(() -> new ReaderNotFoundException("Читатель с ID " + readerId + " не найден"));
        }

        List<Book> books = loanRepository.findBooksIssuedToReader(readerId);
        try (Connection conn = DatabaseConfig.getConnection()) {
            return books.stream().map(book -> {
                try {
                    int total = bookItemRepository.countByBookId(book.getId(), conn);
                    int available = bookItemRepository.countAvailableByBookId(book.getId(), conn);
                    return BookMapper.toDto(book, total, available);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
        }
    }

    @Override
    public List<BookDto> getPopularBooks() throws SQLException {
        return loanRepository.getPopularBooks(DEFAULT_TOP_SIZE);
    }

    @Override
    public List<LoanDto> getAllBorrowedBooks() throws SQLException {
        List<Loan> loans = loanRepository.findAllActiveLoans();
        return LoanMapper.toDtoList(loans);
    }

    private Long getStatusIdByName(String name, Connection conn) throws SQLException {
        String sql = "SELECT id FROM item_status WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
                throw new SQLException("Status not found: " + name);
            }
        }
    }

    @Override
    public List<ActiveLoanInfoDto> getAllActiveLoansWithDetails() throws SQLException {
        return loanRepository.findAllActiveLoansWithDetails();
    }

    @Override
    public List<BookWithBorrowDateDto> getBooksIssuedToReaderWithDates(Long readerId) throws SQLException, ReaderNotFoundException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            readerRepository.findById(readerId, conn)
                    .orElseThrow(() -> new ReaderNotFoundException("Читатель с ID " + readerId + " не найден"));
        }
        return loanRepository.findBooksIssuedToReaderWithDates(readerId);
    }
}