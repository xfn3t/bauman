package ru.bauman.repository;

import ru.bauman.common.repository.Repository;
import ru.bauman.dto.ActiveLoanInfoDto;
import ru.bauman.dto.BookDto;
import ru.bauman.dto.BookWithBorrowDateDto;
import ru.bauman.model.Book;
import ru.bauman.model.Loan;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends Repository<Loan, Long> {
    Optional<Loan> findActiveLoanByBookItemId(Long bookItemId, Connection conn) throws SQLException;
    List<Loan> findActiveLoansByReaderId(Long readerId) throws SQLException;
    List<ActiveLoanInfoDto> findAllActiveLoansWithDetails() throws SQLException;
    void returnBook(Long loanId, Connection conn) throws SQLException;
    List<Loan> findAllActiveLoans() throws SQLException;
    List<BookDto> getPopularBooks(int limit) throws SQLException;
    List<Book> findBooksIssuedToReader(Long readerId) throws SQLException;
    List<BookWithBorrowDateDto> findBooksIssuedToReaderWithDates(Long readerId) throws SQLException;
}