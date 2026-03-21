package ru.bauman.serive.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ru.bauman.common.exception.BookNotAvailableException;
import ru.bauman.common.exception.BookNotFoundException;
import ru.bauman.common.exception.ReaderNotFoundException;
import ru.bauman.config.DatabaseConfig;
import ru.bauman.dto.BookDto;
import ru.bauman.dto.LoanDto;
import ru.bauman.mapper.BookMapper;
import ru.bauman.mapper.LoanMapper;
import ru.bauman.model.Book;
import ru.bauman.model.Loan;
import ru.bauman.model.Reader;
import ru.bauman.repository.BookRepository;
import ru.bauman.repository.LoanRepository;
import ru.bauman.repository.ReaderRepository;
import ru.bauman.serive.LoanService;

@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final LoanRepository loanRepository;

    @Override
    public void issueBook(Long bookId, Long readerId) throws SQLException, BookNotFoundException, ReaderNotFoundException, BookNotAvailableException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Book book = bookRepository.findById(bookId, conn)
                        .orElseThrow(() -> new BookNotFoundException("Книга с ID " + bookId + " не найдена"));
                if (!book.isAvailable()) {
                    throw new BookNotAvailableException("Книга уже выдана");
                }

                Reader reader = readerRepository.findById(readerId, conn)
                        .orElseThrow(() -> new ReaderNotFoundException("Читатель с ID " + readerId + " не найден"));

                Loan loan = new Loan();
                loan.setBookId(bookId);
                loan.setReaderId(readerId);
                loan.setBorrowDate(new Timestamp(System.currentTimeMillis()));
                loanRepository.save(loan, conn);

                bookRepository.updateAvailability(bookId, false, conn);
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
                if (book.isAvailable()) {
                    throw new BookNotAvailableException("Книга не была выдана");
                }

                Loan loan = loanRepository.findActiveLoanByBookId(bookId, conn)
                        .orElseThrow(() -> new BookNotAvailableException("Нет активной выдачи для этой книги"));

                loanRepository.returnBook(loan.getId(), conn);
                bookRepository.updateAvailability(bookId, true, conn);
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
        return BookMapper.toDtoList(books);
    }

    @Override
    public List<BookDto> getPopularBooks() throws SQLException {
        return loanRepository.getPopularBooks(10);
    }

    @Override
    public List<LoanDto> getAllBorrowedBooks() throws SQLException {
        List<Loan> loans = loanRepository.findAllActiveLoans();
        return LoanMapper.toDtoList(loans);
    }
}