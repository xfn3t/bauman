package ru.bauman.serive.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import ru.bauman.repository.BookRepository;
import ru.bauman.repository.BookItemRepository;
import ru.bauman.repository.ItemStatusRepository;
import ru.bauman.model.Book;
import ru.bauman.model.BookItem;
import ru.bauman.mapper.BookMapper;
import ru.bauman.config.DatabaseConfig;
import ru.bauman.dto.BookDto;

@RequiredArgsConstructor
public class BookServiceImpl implements ru.bauman.serive.BookService {

    private final BookRepository bookRepository;
    private final BookItemRepository bookItemRepository;
    private final ItemStatusRepository itemStatusRepository;

    @Override
    public BookDto addBook(BookDto bookDto) throws SQLException {
        Book book = BookMapper.toEntity(bookDto);
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                book = bookRepository.save(book, conn);

                Long availableStatusId = itemStatusRepository.getStatusIdByName("AVAILABLE", conn)
                        .orElseThrow(() -> new SQLException("Status 'AVAILABLE' not found"));

                for (int i = 0; i < bookDto.totalCopies(); i++) {
                    BookItem item = new BookItem(null, book.getId(), availableStatusId, null, null);
                    bookItemRepository.save(item, conn);
                }

                conn.commit();

                int total = bookItemRepository.countByBookId(book.getId(), conn);
                int available = bookItemRepository.countAvailableByBookId(book.getId(), conn);
                return BookMapper.toDto(book, total, available);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<BookDto> getAllBooks() throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            List<Book> books = bookRepository.findAll();
            List<BookDto> dtos = new ArrayList<>();
            for (Book book : books) {
                int total = bookItemRepository.countByBookId(book.getId(), conn);
                int available = bookItemRepository.countAvailableByBookId(book.getId(), conn);
                dtos.add(BookMapper.toDto(book, total, available));
            }
            return dtos;
        }
    }

    @Override
    public Optional<BookDto> findBookByTitle(String title) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            Optional<Book> bookOpt = bookRepository.findByTitle(title, conn);
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                int total = bookItemRepository.countByBookId(book.getId(), conn);
                int available = bookItemRepository.countAvailableByBookId(book.getId(), conn);
                return Optional.of(BookMapper.toDto(book, total, available));
            }
            return Optional.empty();
        }
    }
}