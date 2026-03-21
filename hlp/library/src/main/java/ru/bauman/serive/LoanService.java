package ru.bauman.serive;

import java.sql.SQLException;
import java.util.List;
import ru.bauman.common.exception.BookNotAvailableException;
import ru.bauman.common.exception.BookNotFoundException;
import ru.bauman.common.exception.ReaderNotFoundException;
import ru.bauman.dto.BookDto;
import ru.bauman.dto.LoanDto;

public interface LoanService {
    void issueBook(Long bookId, Long readerId) throws SQLException, BookNotFoundException, ReaderNotFoundException, BookNotAvailableException;
    void returnBook(Long bookId) throws SQLException, BookNotFoundException, BookNotAvailableException;
    List<BookDto> getBooksIssuedToReader(Long readerId) throws SQLException, ReaderNotFoundException;
    List<BookDto> getPopularBooks() throws SQLException;
    List<LoanDto> getAllBorrowedBooks() throws SQLException;
}