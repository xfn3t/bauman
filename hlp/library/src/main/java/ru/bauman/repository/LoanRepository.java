package ru.bauman.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ru.bauman.model.Loan;
import ru.bauman.model.Book;
import ru.bauman.dto.BookDto;

public interface LoanRepository extends ru.bauman.common.repository.Repository<Loan, Long> {
    Optional<Loan> findActiveLoanByBookId(Long bookId, Connection conn) throws SQLException;
    List<Loan> findActiveLoansByReaderId(Long readerId) throws SQLException;
    void returnBook(Long loanId, Connection conn) throws SQLException;
    List<Loan> findAllActiveLoans() throws SQLException;
    List<BookDto> getPopularBooks(int limit) throws SQLException;
    List<Book> findBooksIssuedToReader(Long readerId) throws SQLException;
}