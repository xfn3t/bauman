package ru.bauman.serive.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import ru.bauman.repository.BookRepository;
import ru.bauman.model.Book;
import ru.bauman.mapper.BookMapper;
import ru.bauman.config.DatabaseConfig;
import ru.bauman.dto.BookDto;

@RequiredArgsConstructor
public class BookServiceImpl implements ru.bauman.serive.BookService {

    private final BookRepository bookRepository;

    @Override
    public BookDto addBook(BookDto bookDto) throws SQLException {
        Book book = BookMapper.toEntity(bookDto);
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                bookRepository.save(book, conn);
                conn.commit();
                return BookMapper.toDto(book);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<BookDto> getAllBooks() throws SQLException {
        List<Book> books = bookRepository.findAll();
        return BookMapper.toDtoList(books);
    }

    @Override
    public Optional<BookDto> findBookByTitle(String title) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return bookRepository.findByTitle(title, conn)
                    .map(BookMapper::toDto);
        }
    }
}