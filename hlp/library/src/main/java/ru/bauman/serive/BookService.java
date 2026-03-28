package ru.bauman.serive;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import ru.bauman.dto.BookDto;

public interface BookService {
    BookDto addBook(BookDto bookDto) throws SQLException;
    List<BookDto> getAllBooks() throws SQLException;
    Optional<BookDto> findBookByTitle(String title) throws SQLException;
}